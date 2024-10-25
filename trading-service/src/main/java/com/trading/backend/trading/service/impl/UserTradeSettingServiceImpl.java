package com.google.backend.trading.service.impl;

import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.trading.component.TimeZone;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeUserTradeSettingMapper;
import com.google.backend.trading.dao.mapper.DefaultUserSystemSettingMapper;
import com.google.backend.trading.dao.mapper.TradeUserTradeSettingMapper;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.dao.model.TradeUserSystemSettingExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.dao.model.TradeUserTradeSettingExample;
import com.google.backend.trading.mapstruct.config.TradeUserTradeSettingMapStruct;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.trade.MarginPositionLimitEnum;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户交易设置
 *
 * @author adam.wang
 * @date 2021/10/4 10:41
 */
@Slf4j
@Service
public class UserTradeSettingServiceImpl implements UserTradeSettingService {

	@Resource
	private DefaultTradeUserTradeSettingMapper defaultTradeUserTradeSettingMapper;
	@Autowired
	private TradeUserTradeSettingMapper tradeSettingMapper;
	@Autowired
	private DefaultTradePositionMapper defaultTradePositionMapper;
	@Resource
	private TradeUserTradeSettingMapStruct tradeUserTradeSettingMapStruct;
	@Resource
	private DefaultUserSystemSettingMapper defaultUserSystemSettingMapper;
	@Resource
	private TradeAssetService tradeAssetService;
	@Resource
	private AssetRequest assetRequest;


	@Override
	public TradeUserTradeSetting queryTradeSettingByUid(String uid) {
		TradeUserTradeSettingExample example = new TradeUserTradeSettingExample();
		TradeUserTradeSettingExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		List<TradeUserTradeSetting> tradeUserTradeSettings = defaultTradeUserTradeSettingMapper.selectByExample(example);
		if (tradeUserTradeSettings.isEmpty()) {
			log.info("uid = {}, default setting init", uid);
			TradeUserTradeSetting setting = new TradeUserTradeSetting();
			setting.setUid(uid);
			setting.setLeverage(BigDecimal.ONE);
			setting.setMaxLoss(BigDecimal.ZERO);
			setting.setTakeProfit(BigDecimal.ZERO);
			setting.setEarnPledge(false);
			setting.setDefaultCoin(Constants.DEFAULT_COIN);
			setting.setSettleTimeZoneId(TimeZone.UTC_0800_Beijing.getId());
			setting.setAutoSettle(false);
			setting.setAutoFixNegative(true);
			setting.setLiquidEarn(false);
			setting.setEmailNotification(true);
			setting.setReconfirmOrder(true);
			setting.setDoubleClickPlaceOrder(false);
			setting.setAutoConvert(false);
			setting.setLastSettleTimeZoneId(TimeZone.UTC_0800_Beijing.getId());
			setting.setSettleTimeNextEffectiveTime(new Date());
			tradeSettingMapper.insertIgnoreConflict(setting);
			return setting;
		}
		return tradeUserTradeSettings.get(0);
	}

	@Override
	public UserTradeSettingVo queryTradeSetting(String uid) {
		TradeUserTradeSetting tradeUserTradeSetting = this.queryTradeSettingByUid(uid);
		Map<String, PoolEntityForRisk> poolEntityForRiskMap = assetRequest.assetPoolForRisk(uid);
		TradePositionExample example = new TradePositionExample();
		TradePositionExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
		criteria.andUidEqualTo(uid);
		List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
		MarginInfo marginInfo = tradeAssetService.marginInfo(uid, poolEntityForRiskMap, tradePositions,
				tradeUserTradeSetting.getLeverage(), tradeUserTradeSetting.getEarnPledge(), true);
		return tradeUserTradeSetting2Vo(tradeUserTradeSetting, marginInfo);
	}

	@Override
	public List<UserSettingRes> queryUserSetting(List<String> uids) {
		//查询清算开关
		TradeUserSystemSettingExample sysExample = new TradeUserSystemSettingExample();
		sysExample.createCriteria().andUidIn(uids).andLiquidationEqualTo(false);
		List<TradeUserSystemSetting> tradeUserSystemSettings = defaultUserSystemSettingMapper.selectByExample(sysExample);

		TradeUserTradeSettingExample example = new TradeUserTradeSettingExample();
		TradeUserTradeSettingExample.Criteria criteria = example.createCriteria();
		criteria.andUidIn(uids);
		List<TradeUserTradeSetting> tradeUserTradeSettings = defaultTradeUserTradeSettingMapper.selectByExample(example);
		List<UserSettingRes> userSettingRes = tradeUserTradeSettingMapStruct.tradeUserTradeSettings2UserSettingRes(tradeUserTradeSettings);

		if (ListUtil.isNotEmpty(tradeUserSystemSettings)) {
			Map<String, Boolean> map = tradeUserSystemSettings.stream().collect(Collectors.toMap(TradeUserSystemSetting::getUid,
					TradeUserSystemSetting::getLiquidation));
			userSettingRes.forEach(item -> {
				if (!Objects.isNull(map.get(item.getUid()))) {
					item.setLiquidation(false);
				}
			});
		}
		return userSettingRes;
	}

	@Override
	public UserTradeSettingVo tradeUserTradeSetting2Vo(TradeUserTradeSetting tradeUserTradeSetting, MarginInfo marginInfo) {
		UserTradeSettingVo vo = tradeUserTradeSettingMapStruct.tradeUserTradeSetting2Vo(tradeUserTradeSetting);
		vo.setPositionLimit(MarginPositionLimitEnum.totalPositionLimit(vo.getLeverage()));
		vo.setMinStopLoss(calMinStopLoss(marginInfo.getUnpnl()));
		vo.setMaxStopLoss(Constants.TRADE_SETTING_STOP_LOSS_MAX);
		vo.setMinTakeProfit(calMinTakeProfit(marginInfo.getUnpnl()));
		vo.setMaxTakeProfit(Constants.TRADE_SETTING_TASK_PROFIT_MAX);
		String settleTimeZoneId = vo.getSettleTimeZoneId();
		vo.setZoneOffsetSeconds(TimeZone.getById(settleTimeZoneId).getZoneOffsetSeconds());
		return vo;
	}

	@Override
	public BigDecimal calMinStopLoss(BigDecimal unpnl) {
		return unpnl.multiply(BigDecimal.ONE.add(Constants.TRADE_SETTING_TASK_PROFIT_OR_STOP_LOSS_BUFFER)).min(BigDecimal.ONE.negate()).abs().setScale(Constants.USD_PRECISION, RoundingMode.DOWN);
	}

	@Override
	public BigDecimal calMaxStopLoss(BigDecimal totalMargin) {
		return totalMargin.multiply(Constants.TRADE_SETTING_MAX_STOP_LOSS_PERCENTAGE);
	}

	@Override
	public BigDecimal calMinTakeProfit(BigDecimal unpnl) {
		return unpnl.multiply(BigDecimal.ONE.add(Constants.TRADE_SETTING_TASK_PROFIT_OR_STOP_LOSS_BUFFER)).max(BigDecimal.ONE).setScale(Constants.USD_PRECISION, RoundingMode.DOWN);
	}
}
