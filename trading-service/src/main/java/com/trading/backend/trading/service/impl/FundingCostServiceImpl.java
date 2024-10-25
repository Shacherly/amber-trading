package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.backend.asset.common.model.asset.req.BatchUserPoolReq;
import com.google.backend.asset.common.model.asset.res.NegBalanceUserRes;
import com.google.backend.asset.common.model.base.BatchPoolEntity;
import com.google.backend.asset.common.model.trade.req.TradeFundingCostReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetInfoClient;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeFundingRateMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeNegativeBalanceFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionFundingCostMapper;
import com.google.backend.trading.dao.mapper.TradeFeeConfigMapper;
import com.google.backend.trading.dao.mapper.TradeFundingRateMapper;
import com.google.backend.trading.dao.mapper.TradeNegativeBalanceFundingCostMapper;
import com.google.backend.trading.dao.mapper.TradePositionFundingCostMapper;
import com.google.backend.trading.dao.model.TradeFundingRate;
import com.google.backend.trading.dao.model.TradeFundingRateExample;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCostExample;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCostExample;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.TradeFundingRateMapStruct;
import com.google.backend.trading.mapstruct.margin.PositionFundingCostMapStruct;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.funding.api.FundingRateHistoryReq;
import com.google.backend.trading.model.funding.api.FundingRateHistoryRes;
import com.google.backend.trading.model.funding.api.FundingRateRes;
import com.google.backend.trading.model.funding.dto.FundingCostDto;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;
import com.google.backend.trading.model.internal.amp.AmpPositionFundReq;
import com.google.backend.trading.model.internal.amp.AmpPositionFundRes;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.push.FundingBehaviorEventMessage;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.service.FundingCostService;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.trace.annotation.TraceId;
import com.google.backend.trading.transaction.MarginTransaction;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 资金费率相关
 *
 * @author adam.wang
 * @date 2021/9/30 11:16
 */
@Slf4j
@Service
public class FundingCostServiceImpl implements FundingCostService {

    @Resource
    private DefaultTradeFundingRateMapper defaultTradeFundingRateMapper;
    @Resource
    TradeFundingRateMapStruct tradeFundingRateMapStruct;
    @Autowired
    private MarginService marginService;

    @Autowired
    private TradeFundingRateMapper fundingRateMapper;

    @Autowired
    private TradePositionFundingCostMapper positionFundingCostMapper;

    @Resource
    private DefaultTradePositionFundingCostMapper defaultTradePositionFundingCostMapper;

    @Resource
    private PositionFundingCostMapStruct positionFundingCostMapStruct;

    @Autowired
    private TradeNegativeBalanceFundingCostMapper negativeBalanceFundingCostMapper;

    @Autowired
    private DefaultTradeNegativeBalanceFundingCostMapper defaultNegativeBalanceFundingCostMapper;

    @Autowired
    private AssetTradeClient assetTradeClient;

    @Autowired
    private AssetInfoClient assetInfoClient;

    @Autowired
    private MarginTransaction marginTransaction;

    @Autowired
    private PushComponent pushComponent;
    @Autowired
    private PushMsgService pushService;

    @Autowired
    private TradeFeeConfigMapper feeConfigMapper;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;


    @Override
    public List<FundingRateRes> realTimeRate() {
        return CoinDomain.CACHE.values().stream().filter(v -> !v.getName().equals(Constants.IDK_COIN)).sorted((o1, o2) -> {
            if (null == o1.getCommonConfig() || null == o2.getCommonConfig()) {
                return o1.getName().compareTo(o2.getName());
            }
            return o1.getCommonConfig().getPriority() - o2.getCommonConfig().getPriority();
        }).map(coinDomain -> {
            FundingRateRes fundingRate = new FundingRateRes();
            fundingRate.setCoin(coinDomain.getName());
            fundingRate.setBorrow(coinDomain.getBorrow());
            fundingRate.setLend(coinDomain.getLend());
            return fundingRate;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<FundingRateHistoryRes> listHistoryRate(FundingRateHistoryReq fundingRateHistoryReq) {
        PageHelper.startPage(fundingRateHistoryReq.getPage(), fundingRateHistoryReq.getPageSize(), true);
        List<TradeFundingRate> tradeFundingRates = getTradeFundingRates(fundingRateHistoryReq.getCoin());
        PageInfo<TradeFundingRate> pageInfo = new PageInfo<>(tradeFundingRates);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(),
                tradeFundingRateMapStruct.tradeFundingRate2fundingRateHistoryRes(tradeFundingRates));
    }

    /**
     * 查询 近一年的
     *
     * @param coin
     * @return
     */
    private List<TradeFundingRate> getTradeFundingRates(String coin) {
        TradeFundingRateExample example = new TradeFundingRateExample();
        TradeFundingRateExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(coin)) {
            criteria.andCoinEqualTo(coin);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        criteria.andTimeGreaterThanOrEqualTo(c.getTime());
        criteria.andShowEqualTo(true);
        example.setOrderByClause("TIME DESC");
        List<TradeFundingRate> tradeFundingRates = defaultTradeFundingRateMapper.selectByExample(example);
        return tradeFundingRates;
    }

    @Override
    public List<FundingRateHistoryRes> listRateByCoin(String coin) {
        List<TradeFundingRate> tradeFundingRates = getTradeFundingRates(coin);
        return tradeFundingRateMapStruct.tradeFundingRate2fundingRateHistoryRes(tradeFundingRates);
    }


    @Override
    public void batchSaveHalfHourHistory() {
        boolean show = false;
        Calendar saveTimeCalendar = tradeFundingRateTime();
        if (saveTimeCalendar.get(Calendar.HOUR_OF_DAY) == Constants.SETTLE_HOUR_OF_DAY && saveTimeCalendar.get(Calendar.MINUTE) == 0) {
            //判断是否是4点时刻
            show = true;
        }
        boolean finalShow = show;
        List<TradeFundingRate> fundingRates = CoinDomain.CACHE.values().stream()
                .filter(coinDomain -> null != coinDomain.getLend() && null != coinDomain.getBorrow())
                .map(coinDomain -> {
                    TradeFundingRate rate = new TradeFundingRate();
                    rate.setCoin(coinDomain.getName());
                    rate.setLend(coinDomain.getLend());
                    rate.setBorrow(coinDomain.getBorrow());
                    long fundingRateTs = coinDomain.getFundingRateTs();
                    rate.setTime(saveTimeCalendar.getTime());
                    rate.setShow(finalShow);
                    log.info("historyToDb, fundingRateTs = {}, data = {}", fundingRateTs, rate);
                    return rate;
                }).collect(Collectors.toList());
        fundingRateMapper.batchInsertIgnoreConflict(fundingRates);
    }

    private Calendar tradeFundingRateTime() {
        Calendar time = Calendar.getInstance();
        int minute = time.get(Calendar.MINUTE);
        if (minute < 30) {
            minute = 0;
        } else {
            minute = 30;
        }
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        return time;
    }

    @Override
    public void settlePositionFundingCost(List<String> uidList, Date settleTime) {
        for (String uid : uidList) {
            try {
                settleUserPositionFundingCost(uid, settleTime);
            } catch (Exception e) {
                log.error("settlePositionFundingCost err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
            }
        }
    }

    private void settleUserPositionFundingCost(String uid, Date settleTime) {
        Date now = new Date();
        List<TradePosition> tradePositions = marginService.listAllActivePositions(Collections.singletonList(uid));
        if (CollectionUtils.isEmpty(tradePositions)) {
            return;
        }
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(uid);
        List<TradePositionFundingCost> fundingCosts = new ArrayList<>();
        BigDecimal totalFundingCost = BigDecimal.ZERO;
        for (TradePosition tradePosition : tradePositions) {
            String symbol = tradePosition.getSymbol();
            String direction = tradePosition.getDirection();
            BigDecimal quantity = tradePosition.getQuantity();
            FundingCostDto dto = CoinDomain.positionFundingCostDto(symbol, direction, quantity);
            BigDecimal fundingCost = dto.getFundingCost().multiply(userFeeConfigRate.getFundingCostRate());
            totalFundingCost = totalFundingCost.add(fundingCost);
            TradePositionFundingCost cost = new TradePositionFundingCost();
            cost.setUuid(UUID.randomUUID().toString());
            cost.setUid(uid);
            cost.setStatus(FundingCostStatus.PENDING.getName());
            cost.setQuantity(quantity);
            cost.setFundingCost(fundingCost);
            cost.setPositionId(tradePosition.getUuid());
            cost.setSymbol(symbol);
            cost.setDirection(direction);
            cost.setCoin(Constants.BASE_COIN);
            cost.setCtime(now);
            cost.setMtime(now);
            cost.setPrice(dto.getSettlePrice());
            cost.setLend(dto.getLend());
            cost.setBorrow(dto.getBorrow());
            cost.setRound(settleTime.getTime());
            fundingCosts.add(cost);
            FundingBehaviorEventMessage msg = FundingBehaviorEventMessage.buildMarginCommission(cost);
            pushComponent.pushFundingEventMessage(msg);
        }
        try {
            positionFundingCostMapper.batchInsert(fundingCosts);
        } catch (DuplicateKeyException e) {
            log.error("positionFundingCostMapper DuplicateKeyException, cause = {}",
                    ExceptionUtils.getRootCauseMessage(e), e);
            return;
        }
        List<Long> idList = fundingCosts.stream().map(TradePositionFundingCost::getId).collect(Collectors.toList());
        TradeFundingCostReq req = new TradeFundingCostReq();
        Optional<String> minUuid = fundingCosts.stream().map(TradePositionFundingCost::getUuid
        ).min(Comparator.comparing(UUID::fromString));
        String reqId = null;
        if (minUuid.isPresent()) {
            reqId = minUuid.get();
        }
        log.info("asset, do position funding cost, reqId = {}, idList = {}, totalFundingCost = {}", reqId, idList, totalFundingCost);
        if (totalFundingCost.compareTo(BigDecimal.ZERO) != 0) {
            req.setReqId(reqId);
            TradeFundingCostReq.Params params = new TradeFundingCostReq.Params();
            params.setUid(uid);
            params.setCoinAndAmount(ImmutableMap.of(Constants.BASE_COIN, totalFundingCost));
            req.setParams(params);
            Response<Void> res = assetTradeClient.doFundingCost(req, new LinkedMultiValueMap<>());
            if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode()) {
                AlarmLogUtil.alarm("do position funding cost err, req = {}, res = {}", req, res);
                return;
            } else {
                pushService.marginSettleDone(uid, totalFundingCost);
            }
        }
        for (TradePositionFundingCost fundingCost : fundingCosts) {
            if (!marginTransaction.updatePositionFundingCost(fundingCost.getPositionId(), fundingCost.getId(),
                    fundingCost.getFundingCost())) {
                log.error("update funding cost and position pnl fail, data = {}", fundingCost);
            }
        }
    }

    @TraceId
    @Override
    public void settleNegativeBalanceFundingCost(List<String> uidList, Date settleTime) {
        Response<List<NegBalanceUserRes>> negBalanceRes = assetInfoClient.getNegBalanceUser();
        if (BusinessExceptionEnum.SUCCESS.getCode() != negBalanceRes.getCode()) {
            log.error("asset getNegBalanceUser err, negBalanceRes = {}", negBalanceRes);
            return;
        }
        List<NegBalanceUserRes> list = negBalanceRes.getData();
        List<String> needSettleUidList =
                list.stream().map(NegBalanceUserRes::getUid).filter(uidList::contains).distinct().collect(Collectors.toList());
        int totalSize = needSettleUidList.size();
        if (0 == totalSize) {
            return;
        }
        int fromIndex = 0;
        int toIndex;
        int offset = 1000;
        int round = 1;
        do {
            log.info("settleNegativeBalanceFundingCost round = {}", round++);
            toIndex = Math.min(fromIndex + offset, totalSize);
            BatchUserPoolReq poolReq = new BatchUserPoolReq();
            poolReq.setUids(needSettleUidList.subList(fromIndex, toIndex));
            Response<List<BatchPoolEntity>> poolRes = assetInfoClient.getPool(poolReq);
            if (BusinessExceptionEnum.SUCCESS.getCode() != poolRes.getCode()) {
                log.error("asset getPool err, poolRes = {}", poolRes);
                continue;
            }
            List<BatchPoolEntity> poolEntities = poolRes.getData();
            if (CollectionUtils.isEmpty(poolEntities)) {
                return;
            }
            for (BatchPoolEntity poolEntity : poolEntities) {
                String uid = poolEntity.getUid();
                UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(uid);
                Map<String, BigDecimal> negBalanceMap =
                        poolEntity.getPools().entrySet().stream().filter(entry -> entry.getValue().getBalance().compareTo(BigDecimal.ZERO) < 0)
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getBalance()));
                List<TradeNegativeBalanceFundingCost> fundingCosts = new ArrayList<>();
                Map<String, BigDecimal> negFundingCostMap = new HashMap<>();
                Date now = new Date();
                for (Map.Entry<String, BigDecimal> entry : negBalanceMap.entrySet()) {
                    String coin = entry.getKey();
                    BigDecimal balance = entry.getValue();
                    FundingCostDto dto = CoinDomain.negativeBalanceFundingCostDto(coin, balance);
                    BigDecimal fundingCost = dto.getFundingCost().multiply(userFeeConfigRate.getFundingCostRate());
                    TradeNegativeBalanceFundingCost cost = new TradeNegativeBalanceFundingCost();
                    cost.setUuid(UUID.randomUUID().toString());
                    cost.setUid(uid);
                    cost.setStatus(FundingCostStatus.PENDING.getName());
                    cost.setQuantity(balance);
                    cost.setFundingCost(fundingCost);
                    cost.setCoin(coin);
                    cost.setPrice(null);
                    cost.setLend(dto.getLend());
                    cost.setBorrow(dto.getBorrow());
                    cost.setCtime(now);
                    cost.setMtime(now);
                    cost.setRound(settleTime.getTime());
                    fundingCosts.add(cost);
                    if (fundingCost.compareTo(BigDecimal.ZERO) != 0) {
                        negFundingCostMap.put(coin, fundingCost);
                    }
                    FundingBehaviorEventMessage msg = FundingBehaviorEventMessage.buildNegativeBalance(cost);
                    pushComponent.pushFundingEventMessage(msg);
                }
                try {
                    negativeBalanceFundingCostMapper.batchInsert(fundingCosts);
                } catch (DuplicateKeyException e) {
                    log.error("negativeBalanceFundingCostMapper DuplicateKeyException, cause = {}", ExceptionUtils.getRootCauseMessage(e)
                            , e);
                    return;
                }
                //TODO 转换
                List<Long> idList = fundingCosts.stream().map(TradeNegativeBalanceFundingCost::getId).collect(Collectors.toList());
                TradeFundingCostReq req = new TradeFundingCostReq();
                Optional<String> minUuid =
                        fundingCosts.stream().map(TradeNegativeBalanceFundingCost::getUuid).min(Comparator.comparing(UUID::fromString));
                String reqId = null;
                if (minUuid.isPresent()) {
                    reqId = minUuid.get();
                }
                log.info("asset, do NegativeBalance funding cost, reqId = {}, idList = {}", reqId, idList);
                req.setReqId(reqId);
                TradeFundingCostReq.Params params = new TradeFundingCostReq.Params();
                params.setUid(uid);
                params.setCoinAndAmount(negFundingCostMap);
                req.setParams(params);
                Response<Void> res = assetTradeClient.doFundingCost(req, new LinkedMultiValueMap<>());
                if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode()) {
                    log.error("asset, do NegativeBalance funding cost err, req = {}, res = {}", req, res);
                } else {
                    TradeNegativeBalanceFundingCost update = new TradeNegativeBalanceFundingCost();
                    update.setStatus(FundingCostStatus.COMPLETED.getName());
                    TradeNegativeBalanceFundingCostExample example = new TradeNegativeBalanceFundingCostExample();
                    example.createCriteria().andIdIn(idList).andStatusEqualTo(FundingCostStatus.PENDING.getName());
                    int i = defaultNegativeBalanceFundingCostMapper.updateByExampleSelective(update, example);
                    if (i != idList.size()) {
                        log.error("update records size not match insert records, update size = {}, insert size = {}", i, idList.size());
                    }
                }
            }
            fromIndex += offset;
        } while (toIndex < totalSize);
    }

    @Override
    public PageResult<AmpPositionFundRes> getHistoryFundForAmp(AmpPositionFundReq ampPositionFundReq) {
        TradePositionFundingCostExample tradePositionFundingCostExample = new TradePositionFundingCostExample();
        TradePositionFundingCostExample.Criteria criteria = tradePositionFundingCostExample.createCriteria()
                .andUidEqualTo(ampPositionFundReq.getUid());
        if (StringUtils.isNotEmpty(ampPositionFundReq.getActivitiesId())) {
            criteria.andUuidEqualTo(ampPositionFundReq.getActivitiesId());
        }
        if (StringUtils.isNotEmpty(ampPositionFundReq.getPositionId())) {
            criteria.andPositionIdEqualTo(ampPositionFundReq.getPositionId());
        }
        if (StringUtils.isNotEmpty(ampPositionFundReq.getSymbol())) {
            criteria.andSymbolEqualTo(ampPositionFundReq.getSymbol());
        }
        if (StringUtils.isNotEmpty(ampPositionFundReq.getDirection())) {
            criteria.andDirectionEqualTo(ampPositionFundReq.getDirection());
        }
        if (StringUtils.isNotEmpty(ampPositionFundReq.getStatus())) {
            criteria.andStatusEqualTo(ampPositionFundReq.getStatus());
        }
        if (ampPositionFundReq.getStartCtime() != null && ampPositionFundReq.getEndCtime() != null) {
            criteria.andCtimeBetween(new Date(ampPositionFundReq.getStartCtime()), new Date(ampPositionFundReq.getEndCtime()));
        }
        if (ampPositionFundReq.getStartMtime() != null && ampPositionFundReq.getEndMtime() != null) {
            criteria.andMtimeBetween(new Date(ampPositionFundReq.getStartMtime()), new Date(ampPositionFundReq.getEndMtime()));
        }
        tradePositionFundingCostExample.setOrderByClause("MTIME DESC,CTIME DESC");
        PageHelper.startPage(ampPositionFundReq.getPage(), ampPositionFundReq.getPageSize(), true);
        List<TradePositionFundingCost> tradePositionFundingCosts = defaultTradePositionFundingCostMapper.selectByExample(tradePositionFundingCostExample);
        PageInfo<TradePositionFundingCost> pageInfo = new PageInfo<>(tradePositionFundingCosts);
        List<AmpPositionFundRes> list = positionFundingCostMapStruct.positionsFundingCost2AmpPositionFundRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }

}
