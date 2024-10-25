package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.TradeNegativeBalanceFundingCostMapper;
import com.google.backend.trading.dao.mapper.TradePositionFundingCostMapper;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.model.funding.api.FundingRateReq;
import com.google.backend.trading.model.funding.api.FundingRateRes;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;

import org.junit.Test;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author adam.wang
 * @date 2021/9/30 14:25
 */
public class FundingCostServiceTest extends TradingServiceApplicationTest {

    @Resource
    FundingCostService fundingCostService;
    @Resource
    PushMsgService pushService;
    @Resource
    private TradeNegativeBalanceFundingCostMapper tradeNegativeBalanceFundingCostMapper;
    @Resource
    private TradePositionFundingCostMapper tradePositionFundingCostMapper;

    private static final String sitUserId = "619365813eb262c6099da416";

    @Test
    public void testListRate() {

        FundingRateReq fundingRateReq = new FundingRateReq();
        fundingRateReq.setCoin("BTC");
        fundingRateReq.setPageSize(2);
        List<FundingRateRes> res = fundingCostService.realTimeRate();
    }

    @Test
    public void testSettleFundingCost() throws InterruptedException {
        Thread.sleep(5000L);
        fundingCostService.settlePositionFundingCost(Collections.singletonList("615309c065a76ea30fd8a156"), new Date());
    }


    @Test
    public void testCommonPush() {
        pushService.submitOrderOk(sitUserId, false);
        pushService.cancelOrderOk(sitUserId, true);
        pushService.modifyOrderOk(sitUserId, false);
    }

    @Test
    public void testSpotPush() {
        TradeSpotOrder order = new TradeSpotOrder();
        order.setUid(sitUserId);
        order.setSymbol("BTC_USD");
        order.setDirection("SELL");
        order.setTriggerPrice(new BigDecimal("12345"));
        order.setFilledPrice(new BigDecimal("23456"));
        order.setQuantityFilled(new BigDecimal("666"));
        pushService.spotTriggerOk(order);
        pushService.spotOrderTraded(order);
    }

    @Test
    public void testMarginPush() {
        TradeMarginOrder order = new TradeMarginOrder();
        order.setUid(sitUserId);
        order.setSymbol("BTC_USD");
        order.setDirection("SELL");
        order.setTriggerPrice(new BigDecimal("54321"));
        order.setFilledPrice(new BigDecimal("65432"));
        order.setQuantityFilled(new BigDecimal("888"));
        //pushService.marginNotEnough(order);
        //pushService.marginTriggerOk(order);
        //pushService.marginOrderTraded(order);
        pushService.marginForceClose(sitUserId);
    }

    @Test
    public void testPositionPush() {
        TradePosition single = new TradePosition();
        single.setUid(sitUserId);
        single.setSymbol("ETH_USD");
        single.setPrice(new BigDecimal("22344"));
        single.setQuantity(new BigDecimal("1.34"));
        single.setTakeProfitPrice(new BigDecimal("11432"));
        single.setStopLossPrice(new BigDecimal("11234"));
        BigDecimal lastPnl = new BigDecimal("54128");
        single.setPnl(lastPnl);
        BigDecimal lastAmount = new BigDecimal("784521");
        //pushService.marginStopSinglePosition(single, true, lastPnl, lastAmount);
        pushService.marginStopCrossedPosition(sitUserId, new BigDecimal("33455"));

        //pushService.marginSettleDone(sitUserId, new BigDecimal("23.678"));
        //pushService.marginDelivery(sitUserId, false);
        //pushService.marginAutoDelivery(sitUserId);
    }


    @Test
    public void testInsertData() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);

        List<TradePositionFundingCost> testInsertFundingCosts = new ArrayList<>();
        List<TradeNegativeBalanceFundingCost> testInsertNegativeFundingCosts = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            TradePositionFundingCost cost = new TradePositionFundingCost();
            cost.setUuid(UUID.randomUUID().toString());
            cost.setUid("test");
            cost.setPositionId("test");
            cost.setStatus(FundingCostStatus.COMPLETED.getName());
            cost.setFundingCost(new BigDecimal("-1"));
            cost.setMtime(time.getTime());
            cost.setCtime(time.getTime());
            cost.setRound(time.getTimeInMillis());
            cost.setCoin(Constants.BASE_COIN);
            cost.setQuantity(new BigDecimal("-353.61"));
            cost.setLend(BigDecimal.ZERO);
            cost.setBorrow(BigDecimal.ZERO);
            cost.setSymbol("BTC_USDT");
            cost.setDirection("BUY");
            testInsertFundingCosts.add(cost);

            TradeNegativeBalanceFundingCost negativeFundingCost = new TradeNegativeBalanceFundingCost();
            negativeFundingCost.setUuid(UUID.randomUUID().toString());
            negativeFundingCost.setUid("test");
            negativeFundingCost.setStatus(FundingCostStatus.COMPLETED.getName());
            negativeFundingCost.setFundingCost(new BigDecimal("-1"));
            negativeFundingCost.setMtime(time.getTime());
            negativeFundingCost.setCtime(time.getTime());
            negativeFundingCost.setRound(time.getTimeInMillis());
            negativeFundingCost.setCoin(Constants.BASE_COIN);

            negativeFundingCost.setQuantity(new BigDecimal("-353.61"));
            negativeFundingCost.setLend(BigDecimal.ZERO);
            negativeFundingCost.setBorrow(BigDecimal.ZERO);
            testInsertNegativeFundingCosts.add(negativeFundingCost);

            time.add(Calendar.HOUR_OF_DAY, -1);
        }

        tradePositionFundingCostMapper.batchInsert(testInsertFundingCosts);
        tradeNegativeBalanceFundingCostMapper.batchInsert(testInsertNegativeFundingCosts);
    }
}
