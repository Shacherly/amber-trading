package com.google.backend.trading.task.fix;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.ImmutableMap;
import com.google.backend.asset.common.model.trade.req.TradeFundingCostReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeNegativeBalanceFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSwapOrderMapper;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCostExample;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCostExample;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.trace.annotation.TraceId;
import com.google.backend.trading.util.AlarmLogUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2022/2/2 19:18
 */
@Slf4j
@Component
public class BugFixTask {

    @Autowired
    private DefaultTradeSpotOrderMapper spotOrderMapper;
    @Autowired
    private DefaultTradeSwapOrderMapper swapOrderMapper;
    @Autowired
    private AssetRequest assetRequest;
    @Resource
    private DefaultTradePositionFundingCostMapper defaultTradePositionFundingCostMapper;
    @Resource
    private DefaultTradeNegativeBalanceFundingCostMapper defaultTradeNegativeBalanceFundingCostMapper;
    @Autowired
    private AssetTradeClient assetTradeClient;


    @TraceId
    @XxlJob("fixUserLocked")
    public void fixUserLocked() {
//        fix0304UserLocked();
    }

    @TraceId
    @XxlJob("fixFundingCostTask")
    public void fixFundingCostTask() {
        fixFundingCost();
    }

    @TraceId
    @XxlJob("fixNegativeBalanceFundingCostTask")
    public void fixNegativeBalanceFundingCostTask() {
        fixNegativeBalanceFundingCost();
    }

    private void fixNegativeBalanceFundingCost() {
        log.info("fixNegativeBalanceFundingCost start");
        TradeNegativeBalanceFundingCostExample example = new TradeNegativeBalanceFundingCostExample();
        TradeNegativeBalanceFundingCostExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(FundingCostStatus.PENDING.getName());
        criteria.andCtimeBetween(DateUtil.parse("2022-03-27 00:00:00"), DateUtil.parse("2022-04-11 00:00:00"));
        List<TradeNegativeBalanceFundingCost> allNegativeFundingCosts = defaultTradeNegativeBalanceFundingCostMapper.selectByExample(example);
        allNegativeFundingCosts.stream().collect(Collectors.groupingBy(TradeNegativeBalanceFundingCost::getUid))
                .forEach((uid, uidFundingCosts) -> uidFundingCosts.stream().collect(Collectors.groupingBy(TradeNegativeBalanceFundingCost::getRound))
                        .forEach((roundTime, fundingCosts) -> {
                            Map<String, BigDecimal> negFundingCostMap = fundingCosts.stream().filter(e -> e.getFundingCost().compareTo(BigDecimal.ZERO) != 0).
                                    collect(//key
                                            Collectors.groupingBy(TradeNegativeBalanceFundingCost::getCoin,
                                                    //value
                                                    Collectors.mapping(TradeNegativeBalanceFundingCost::getFundingCost, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                            List<Long> idList = fundingCosts.stream().map(TradeNegativeBalanceFundingCost::getId).collect(Collectors.toList());
                            TradeFundingCostReq req = new TradeFundingCostReq();
                            Optional<String> minUuid =
                                    fundingCosts.stream().map(TradeNegativeBalanceFundingCost::getUuid).min(Comparator.comparing(UUID::fromString));
                            String reqId = null;
                            if (minUuid.isPresent()) {
                                reqId = minUuid.get();
                            }
                            log.info("asset, do NegativeBalance funding cost, reqId = {}, idList = {}, totalFundingCost = {}", reqId, idList, negFundingCostMap);
                            req.setReqId(reqId);
                            TradeFundingCostReq.Params params = new TradeFundingCostReq.Params();
                            params.setUid(uid);
                            params.setCoinAndAmount(negFundingCostMap);
                            req.setParams(params);
                            Response<Void> res = assetTradeClient.doFundingCost(req, new LinkedMultiValueMap<>());
                            if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode()) {
                                AlarmLogUtil.alarm("do position funding cost err, req = {}, res = {}", req, res);
                                return;
                            } else {
                                TradeNegativeBalanceFundingCost update = new TradeNegativeBalanceFundingCost();
                                update.setStatus(FundingCostStatus.COMPLETED.getName());
                                update.setMtime(new Date());
                                TradeNegativeBalanceFundingCostExample updateExample = new TradeNegativeBalanceFundingCostExample();
                                updateExample.createCriteria().andIdIn(idList).andStatusEqualTo(FundingCostStatus.PENDING.getName());
                                int i = defaultTradeNegativeBalanceFundingCostMapper.updateByExampleSelective(update, updateExample);
                                if (i != idList.size()) {
                                    log.error("update records size not match insert records, update size = {}, insert size = {}", i, idList.size());
                                }
                            }
                        }));
        log.info("fixNegativeBalanceFundingCost end");
    }

    private void fixFundingCost() {
        log.info("fixFundingCost start");
        TradePositionFundingCostExample example = new TradePositionFundingCostExample();
        TradePositionFundingCostExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(FundingCostStatus.PENDING.getName());
        criteria.andCtimeBetween(DateUtil.parse("2022-03-27 00:00:00"), DateUtil.parse("2022-04-11 00:00:00"));
        List<TradePositionFundingCost> allFundingCosts = defaultTradePositionFundingCostMapper.selectByExample(example);
        allFundingCosts.stream().collect(Collectors.groupingBy(TradePositionFundingCost::getUid)).forEach((uid, uidFundingCosts) -> uidFundingCosts.stream().collect(Collectors.groupingBy(TradePositionFundingCost::getRound)).forEach((roundTime, fundingCosts) -> {
            BigDecimal totalFundingCost = fundingCosts.stream().map(TradePositionFundingCost::getFundingCost).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            List<Long> idList = fundingCosts.stream().map(TradePositionFundingCost::getId).collect(Collectors.toList());
            TradeFundingCostReq req = new TradeFundingCostReq();
            Optional<String> minUuid = fundingCosts.stream().map(TradePositionFundingCost::getUuid).min(Comparator.comparing(UUID::fromString));
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
                }
            }
            for (TradePositionFundingCost fundingCost : fundingCosts) {
                TradePositionFundingCost update = new TradePositionFundingCost();
                update.setId(fundingCost.getId());
                update.setStatus(FundingCostStatus.COMPLETED.getName());
                update.setMtime(new Date());
                boolean success = 1 == defaultTradePositionFundingCostMapper.updateByPrimaryKeySelective(update);
                if (!success) {
                    log.error("update position funding cost err, fundingCost = {}, update = {}", fundingCost, update);
                }
            }

        }));
        log.info("fixFundingCost end");
    }

    public static void main(String[] args) {
        TradeNegativeBalanceFundingCost cost1 = new TradeNegativeBalanceFundingCost();
        cost1.setFundingCost(BigDecimal.ONE);
        cost1.setCoin("USD");
        TradeNegativeBalanceFundingCost cost2 = new TradeNegativeBalanceFundingCost();
        cost2.setFundingCost(BigDecimal.ONE);
        cost2.setCoin("USD");
        TradeNegativeBalanceFundingCost cost3 = new TradeNegativeBalanceFundingCost();
        cost3.setFundingCost(BigDecimal.ONE);
        cost3.setCoin("ETH");
        TradeNegativeBalanceFundingCost cost4 = new TradeNegativeBalanceFundingCost();
        cost4.setFundingCost(BigDecimal.ZERO);
        cost4.setCoin("ETH");
        List<TradeNegativeBalanceFundingCost> fundingCosts = new ArrayList<>();
        fundingCosts.add(cost1);
        fundingCosts.add(cost2);
        fundingCosts.add(cost3);
        fundingCosts.add(cost4);
        Map<String, BigDecimal> negFundingCostMap = fundingCosts.stream().filter(e -> e.getFundingCost().compareTo(BigDecimal.ZERO) != 0).
                collect(//key
                        Collectors.groupingBy(TradeNegativeBalanceFundingCost::getCoin,
                                //value
                                Collectors.mapping(TradeNegativeBalanceFundingCost::getFundingCost, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        System.out.println(negFundingCostMap);

    }

    /**
     * 61d49a79cff29d36ec1bc977 用户 8USD 现货锁定问题
     * https://www.teambition.com/task/61f95454be6056003f2ad70f
     */
//	private void fix0202UserLocked() {
//		String uid = "61d49a79cff29d36ec1bc977";
//		TradeSpotOrderExample example = new TradeSpotOrderExample();
//		example.createCriteria().andIdEqualTo(1548204L).andUidEqualTo(uid);
//		List<TradeSpotOrder> orders = spotOrderMapper.selectByExample(example);
//		log.info("fix user locked order = {}", orders);
//		if (orders.isEmpty()) {
//			log.error("no exist order");
//			return;
//		}
//		TradeSpotOrder order = orders.get(0);
//		String cancelId = "cancel-" + order.getUuid();
//		TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, order.getQuantity(), cancelId);
//		int retCode = assetRequest.cancelFreeze(sendReq);
//		log.info("result code = {}", retCode);
//	}
//	private void fix0215UserLocked() {
//		String uid = "61f8bfbcdb2a90d4c6ce8a4c";
//		TradeSpotOrderExample example = new TradeSpotOrderExample();
//		example.createCriteria().andIdEqualTo(1855438L).andUidEqualTo(uid);
//		List<TradeSpotOrder> orders = spotOrderMapper.selectByExample(example);
//		log.info("fix user locked order = {}", orders);
//		if (orders.isEmpty()) {
//			log.error("no exist order");
//			return;
//		}
//		TradeSpotOrder order = orders.get(0);
//		String cancelId = "cancel-" + order.getUuid();
//		TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, order.getLockAmount(), cancelId);
//		int retCode = assetRequest.cancelFreeze(sendReq);
//		log.info("result code = {}", retCode);
//	}
//    private void fix0226UserLocked() {
//        String uid = "61f933485ac9163326f3a10f";
//        TradeSpotOrderExample example = new TradeSpotOrderExample();
//        example.createCriteria().andIdIn(Lists.newArrayList(1994230L, 1994229L, 1994228L)).andUidEqualTo(uid);
//        List<TradeSpotOrder> orders = spotOrderMapper.selectByExample(example);
//        log.info("fix user locked order = {}", orders);
//        if (orders.isEmpty()) {
//            log.error("no exist order");
//            return;
//        }
//        if (orders.size() != 3) {
//			log.error("order size is wrong");
//			return;
//		}
//		for (TradeSpotOrder order : orders) {
//			String cancelId = "cancel-" + order.getUuid();
//			TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, new BigDecimal("500"), cancelId);
//			int retCode = assetRequest.cancelFreeze(sendReq);
//			log.info("result code = {}", retCode);
//		}
//	}
//    private void fix0304UserLocked() {
//        String uid = "61f933485ac9163326f3a10f";
//        TradeSwapOrderExample example = new TradeSwapOrderExample();
//        example.createCriteria().andIdEqualTo(50923L).andUidEqualTo(uid);
//        List<TradeSwapOrder> orders = swapOrderMapper.selectByExample(example);
//        log.info("fix user locked order = {}", orders);
//        if (orders.isEmpty()) {
//            log.error("no exist order");
//            return;
//        }
//        TradeSwapOrder order = orders.get(0);
//        String cancelId = "cancel-" + order.getUuid();
//        TradeSpotOrderReq sendReq = new TradeSpotOrderReq();
//        sendReq.setReqId(cancelId);
//        TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
//        param.setUid(order.getUid());
//        param.setCoin(order.getFromCoin());
//        param.setCompetitorCoin(order.getToCoin());
//        param.setAmount(new BigDecimal("7.99"));
//        sendReq.setParams(param);
//        int retCode = assetRequest.cancelFreeze(sendReq);
//        log.info("result code = {}", retCode);
//    }

//    @TraceId
//    @XxlJob("fixUserSettle")
//    public void fixUserSettle() {
//        assetRequest.doRollback("f691d409-faaa-4429-9865-a8dd4a3de49b");
//    }
}
