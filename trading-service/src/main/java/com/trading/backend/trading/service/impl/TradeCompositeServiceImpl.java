package com.google.backend.trading.service.impl;

import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.trading.component.TimeZone;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.DefaultUserSystemSettingMapper;
import com.google.backend.trading.dao.mapper.TradeUserTradeSettingMapper;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.dao.model.TradeUserSystemSettingExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.config.TradeUserTradeSettingMapStruct;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.trade.MarginPositionLimitEnum;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.RiskControlService;
import com.google.backend.trading.service.RiskInfoService;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeCompositeService;
import com.google.backend.trading.service.UserTradeSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author trading
 * @date 2021/10/29 16:20
 */
@Slf4j
@Service
public class TradeCompositeServiceImpl implements TradeCompositeService {

    @Autowired
    private AssetRequest assetRequest;

    @Autowired
    private MarginService marginService;

    @Autowired
    private TradeAssetService tradeAssetService;

    @Autowired
    private UserTradeSettingService tradeSettingService;

    @Autowired
    private RiskControlService riskControlService;

    @Autowired
    private TradeUserTradeSettingMapper tradeSettingMapper;
    @Autowired
    private DefaultTradePositionMapper defaultTradePositionMapper;

    @Autowired
    private DefaultTradeTransactionMapper tradeTransactionMapper;

    @Autowired
    private DefaultUserSystemSettingMapper defaultUserSystemSettingMapper;

    @Autowired
    private TradeUserTradeSettingMapStruct tradeSettingMapStruct;

    @Autowired
    private OrderRequest orderRequest;

    @Autowired
    private SensorsTraceService sensorsTraceService;

    @Autowired
    private RiskInfoService riskInfoService;

    @Override
    public UserTradeSettingVo checkAndUpdateUserTradeSetting(TradeUserTradeSetting userTradeSetting, String uid) {
        TradeUserTradeSetting old = tradeSettingService.queryTradeSettingByUid(uid);
        userTradeSetting.setUid(uid);
        String timeZoneId = userTradeSetting.getSettleTimeZoneId();
        BigDecimal newLeverage = null == userTradeSetting.getLeverage() ? old.getLeverage() : userTradeSetting.getLeverage();
        Boolean newEarnPledge = null == userTradeSetting.getEarnPledge() ? old.getEarnPledge() : userTradeSetting.getEarnPledge();
        BigDecimal newTakeProfit = userTradeSetting.getTakeProfit();
        BigDecimal newStopLoss = userTradeSetting.getMaxLoss();
        Boolean newLiquidEarn = userTradeSetting.getLiquidEarn();
        String newTimeZoneId = null == timeZoneId ? old.getSettleTimeZoneId() : timeZoneId;
        //校验时区
        TimeZone.getById(newTimeZoneId);
        //非用户设置 nextEffectiveTime，允许下次修改结算时区的时间
        long nextEffectiveTime = null == old.getSettleTimeNextEffectiveTime() ? 0 :
                old.getSettleTimeNextEffectiveTime().getTime();
        long now = System.currentTimeMillis();
        //结算时区修改检查
        boolean timeZoneUpdate = !newTimeZoneId.equals(old.getSettleTimeZoneId());
        if (timeZoneUpdate) {
            if (nextEffectiveTime > now) {
                throw new BusinessException(BusinessExceptionEnum.CHANGE_TIMEZONE_NOT_SUPPORT);
            }
            userTradeSetting.setSettleTimeNextEffectiveTime(new Date(System.currentTimeMillis() + Duration.ofDays(1).plus(Constants.SETTLE_TIMEZONE_CHANGE_BUFFER_DURATION).toMillis()));
        }

        //杠杆倍数，检查用户杠杆持仓，有杠杆委托订单，不能修改杠杆
        boolean leverageUpdate = newLeverage.compareTo(old.getLeverage()) != 0;
        boolean existMarginActiveOrder = marginService.countActiveOrder(uid) > 0;
        Map<String, PoolEntityForRisk> poolEntityForRiskMap = assetRequest.assetPoolForRisk(uid);
        List<TradePosition> tradePositions = marginService.listAllActivePositions(Collections.singletonList(uid));
        if (leverageUpdate) {
            if (existMarginActiveOrder) {
                throw new BusinessException(BusinessExceptionEnum.EXIST_ORDER_NOT_SUPPORT_CHANGE_LEVERAGE);
            }
            //提高杠杆
            if (newLeverage.compareTo(old.getLeverage()) > 0) {
                BigDecimal positionTotal = BigDecimal.ZERO;
                BigDecimal totalPositionLimit = MarginPositionLimitEnum.totalPositionLimit(newLeverage);
                for (TradePosition position : tradePositions) {
                    BigDecimal positionValue = tradeAssetService.calculatePositionValue(position);
                    BigDecimal positionValueLimit = MarginPositionLimitEnum.positionLimitBySymbol(newLeverage, position.getSymbol());
                    if (positionValue.compareTo(positionValueLimit) > 0) {
                        throw new BusinessException(BusinessExceptionEnum.OVER_POSITION_LIMIT_NOT_SUPPORT_CHANGE_LEVERAGE);
                    }
                    positionTotal = positionTotal.add(positionValue);
                    if (positionTotal.compareTo(totalPositionLimit) > 0) {
                        throw new BusinessException(BusinessExceptionEnum.OVER_POSITION_LIMIT_NOT_SUPPORT_CHANGE_LEVERAGE);
                    }
                }
            } //降低杠杆
            else {
                MarginInfo marginInfo = tradeAssetService.marginInfo(uid, poolEntityForRiskMap, tradePositions, newLeverage,
                        old.getEarnPledge(), true);
                BigDecimal currentLeverage = marginInfo.getCurrentLeverage();
                if (newLeverage.compareTo(currentLeverage) < 0) {
                    throw new BusinessException(BusinessExceptionEnum.MARGIN_INSUFFICIENT_NOT_SUPPORT_CHANGE_LEVERAGE);
                }
            }
        }
        //自动转换开关，无需检查
        //理财质押开关，判断风险率，有杠杆委托订单，不能修改杠杆
        boolean earnPledgeUpdate = !newEarnPledge.equals(old.getEarnPledge());
        if (earnPledgeUpdate) {
            //检测关闭
            if (!newEarnPledge) {
                if (existMarginActiveOrder) {
                    throw new BusinessException(BusinessExceptionEnum.EXIST_ORDER_NOT_SUPPORT_CLOSE_EARN_PLEDGE);
                }
                MarginInfo marginInfo = tradeAssetService.marginInfo(uid, poolEntityForRiskMap, tradePositions, newLeverage,
                        false, true);
                BigDecimal riskRate = marginInfo.getRiskRate();
                if (riskRate.compareTo(Constants.TRADE_SETTING_MARGIN_RISK_THRESHOLD) > 0) {
                    throw new BusinessException(BusinessExceptionEnum.TOO_HIGH_RISK_NOT_SUPPORT_CLOSE_EARN_PLEDGE);
                }
            }
        }
        //自动清算理财开关和理财质押检查风控状态
        boolean liquidEarnUpdate = null != newLiquidEarn && !newLiquidEarn.equals(old.getLiquidEarn());
        if (liquidEarnUpdate || earnPledgeUpdate) {
            riskInfoService.validateRiskStatus(uid);
        }
        //自动平负余额开关，无需检查
        //全仓止盈止损，浮动盈亏校验
        boolean takeProfitUpdate =
                null != newTakeProfit && null != old.getTakeProfit() && newTakeProfit.compareTo(old.getTakeProfit()) != 0
                        && newTakeProfit.compareTo(BigDecimal.ZERO) != 0;
        boolean stopLossUpdate =
                null != newStopLoss && null != old.getMaxLoss() && newStopLoss.compareTo(old.getMaxLoss()) != 0
                        && newStopLoss.compareTo(BigDecimal.ZERO) != 0;
        if (takeProfitUpdate || stopLossUpdate) {
            BigDecimal totalUnpnlUsd = BigDecimal.ZERO;
            for (TradePosition position : tradePositions) {
                BigDecimal unpnlUsd = tradeAssetService.calculatePositionUnpnlUsd(position).getFirst();
                totalUnpnlUsd = totalUnpnlUsd.add(unpnlUsd);
            }
            if (takeProfitUpdate) {
                BigDecimal min = tradeSettingService.calMinTakeProfit(totalUnpnlUsd);
                BigDecimal max = Constants.TRADE_SETTING_TASK_PROFIT_MAX;
                if (newTakeProfit.compareTo(min) < 0 || newTakeProfit.compareTo(max) > 0) {
                    throw new BusinessException(BusinessExceptionEnum.TASK_PROFIT_NOT_SUPPORT);
                }
            }
            if (stopLossUpdate) {
                BigDecimal min = tradeSettingService.calMinStopLoss(totalUnpnlUsd);
                BigDecimal max = Constants.TRADE_SETTING_STOP_LOSS_MAX;
                if (newStopLoss.compareTo(min) < 0 || newStopLoss.compareTo(max) > 0) {
                    throw new BusinessException(BusinessExceptionEnum.STOP_LOSS_NOT_SUPPORT);
                }
            }
        }

        if (Boolean.TRUE.equals(userTradeSetting.getAutoSettle())) {
            //全局自动交割开关，更新所有仓位-》autoSettle
            TradePosition tradePosition = new TradePosition();
            tradePosition.setAutoSettle(true);
            TradePositionExample tradePositionExample = new TradePositionExample();
            tradePositionExample.createCriteria()
                    .andUidEqualTo(userTradeSetting.getUid())
                    .andStatusEqualTo(PositionStatus.ACTIVE.name());
            defaultTradePositionMapper.updateByExampleSelective(tradePosition, tradePositionExample);
        }
        //二次确认开关，无需检查
        //双击下单开关，无需检查
        //交易邮件通知，无需检查
        tradeSettingMapper.updateUserTradeSettingByUid(userTradeSetting);
        TradeUserTradeSetting tradeSetting = tradeSettingService.queryTradeSettingByUid(uid);
        TradeUserSystemSettingExample sysExample = new TradeUserSystemSettingExample();
        sysExample.createCriteria().andUidEqualTo(uid).andLiquidationEqualTo(false);
        List<TradeUserSystemSetting> tradeUserSystemSettings = defaultUserSystemSettingMapper.selectByExample(sysExample);
        boolean liquid = true;
        if (CollectionUtils.isNotEmpty(tradeUserSystemSettings)) {
            liquid = tradeUserSystemSettings.get(0).getLiquidation();
        }
        UserSettingRes res = tradeSettingMapStruct.tradeUserTradeSetting2UserSettingRes(tradeSetting);
        res.setLiquidation(liquid);
        //通知风控，交易设置变更
        riskControlService.userSettingChangeNotice(res);
        // 埋点
        sensorsTraceService.tradeSettingChange(old, tradeSetting);
        MarginInfo marginInfo = tradeAssetService.marginInfo(uid, poolEntityForRiskMap, tradePositions, newLeverage, newEarnPledge, true);
        return tradeSettingService.tradeUserTradeSetting2Vo(tradeSetting, marginInfo);
    }

    @Override
    public boolean isTradeNewUser(String uid) {
        TradeTransactionExample example = new TradeTransactionExample();
        example.createCriteria().andUidEqualTo(uid);
        return tradeTransactionMapper.countByExample(example) == 0;
    }
}
