package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.OrderRes;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSwapConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.spot.api.SpotAvailableReq;
import com.google.backend.trading.model.spot.api.SpotAvailableRes;
import com.google.backend.trading.model.spot.api.SpotOrderActiveReq;
import com.google.backend.trading.model.spot.api.SpotOrderHistoryReq;
import com.google.backend.trading.model.spot.api.SpotOrderInfoRes;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.api.SpotOrderUpdateReq;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PriorityCoinWithUSD;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.SpotUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 现货接口的单元测试
 *
 * @author savion.chen
 * @date 2021/10/10 16:50
 */
public class SpotServiceTest extends TradingServiceApplicationTest {

    @Autowired
    private SpotService spotService;
    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private OrderRequest orderRequest;
    private final static String testSymbol = "ETH_USD";
    private final static String testUser = "61628983d4b1a6d195d6f285";


    @Test
    public void testQueryAvailable() {
        SpotAvailableReq req = new SpotAvailableReq();
        req.setSymbol(testSymbol);

        SpotAvailableRes resp = assetRequest.queryAvailable(req, testUser);
        System.out.println("result=" + resp.toString());
    }


    @Test
    public void testCacheSymbol() {
        SymbolDomain symData = SymbolDomain.nonNullGet("LTC_USD");
        System.out.println("symData=" + symData.toString());
        CoinSymbolConfig cfg = symData.getCoinSymbolConfig();
        System.out.println("getSymbol=" + cfg.toString());

        BigDecimal sellPrice = CommonUtils.getSellPrice("ETH_USD");
        System.out.println("sellPrice=" + sellPrice);
        boolean isOk = symData.checkPlaceOrderPriceStatus();
        System.out.println("isOk=" + isOk);

        CoinDomain btcData = CoinDomain.nonNullGet("USD");
        CoinCommonConfig btcCfg = btcData.getCommonConfig();
        System.out.println("btcCfg=" + btcCfg.toString());
        CoinDomain ethData = CoinDomain.nonNullGet("LTC");
        CoinCommonConfig ethCfg = ethData.getCommonConfig();
        System.out.println("ethCfg=" + ethCfg.toString());
    }

    @Test
    public void testCalcFee() {
        TradeSpotOrder order = new TradeSpotOrder();
        order.setUid(testUser);
        order.setDirection(Direction.BUY.getName());

        CreateTradeRes resp = new CreateTradeRes();
        resp.setStatus("FILLED");
        resp.setFilledPrice(new BigDecimal("1234"));
        resp.setFilled(new BigDecimal("33"));
        resp.setQuoteFilled(new BigDecimal("77"));
        BigDecimal feeQty = orderRequest.calcMiddleFee(order, resp);
        System.out.println("feeQty=" + feeQty);
    }


    @Test
    public void testPlaceOrder() {
        SpotOrderPlace req = new SpotOrderPlace();
        req.setSymbol(testSymbol);
        req.setUid(testUser);

        req.setType(OrderType.LIMIT);
        req.setStrategy(TradeStrategy.FOK);
        req.setDirection(Direction.SELL);
        req.setIsQuote(true);
        req.setQuantity(new BigDecimal("100"));
        req.setPrice(new BigDecimal("3826"));
        req.setNotes("modify");
        req.setSource(SourceType.PLACED_BY_CLIENT);

        SpotOrderPlaceRes res = spotService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res.toString());
        }
    }

    @Test
    public void testLoanPlaceOrder() {
        SpotOrderPlace req = SpotOrderPlace.builder()
                .uid(testUser)
                .symbol(testSymbol)
                .direction(Direction.BUY)
                .isQuote(false)
                .quantity(new BigDecimal("1"))
                .type(OrderType.MARKET)
                .source(SourceType.LOAN_LIQUIDATION)
                .build();
        SpotOrderPlaceRes res = spotService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res.toString());
        };
    }

    @Test
    public void testConversionCoin() {
        SpotOrderPlace req = SpotOrderPlace.builder()
                .uid(testUser)
                .symbol(testSymbol)
                .direction(Direction.BUY)
                .isQuote(true)
                .quantity(new BigDecimal("0.000001"))
                .type(OrderType.MARKET)
                .source(SourceType.LIQUIDATION)
                .build();
        SpotOrderPlaceRes res = spotService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res.toString());
        };
    }

    @Test
    public void testUpdateOrder() {
        String orderId = "dcdee3ac-cb94-4d92-a1a0-511d46d8d4d0";
        SpotOrderUpdateReq req = new SpotOrderUpdateReq();
        req.setOrderId(orderId);
        req.setLastQuantity(new BigDecimal("0.47"));
        req.setLastStatus("EXECUTING");
        req.setQuantity(new BigDecimal("0.8"));
        req.setPrice(new BigDecimal("3456"));
        req.setTriggerPrice(new BigDecimal("3612"));

        String updateId = spotService.updateOrder(req, testUser);
        System.out.println("updateId=" + updateId);
    }

    @Test
    public void testCancelOrder() {
        String orderId = "dcdee3ac-cb94-4d92-a1a0-511d46d8d4d0";
        SpotOrderCancel cancel = new SpotOrderCancel();
        cancel.setOrderId(orderId);
        cancel.setUid(testUser);
        cancel.setTerminator(TradeTerminator.CLIENT);
        String cancelId = spotService.cancelOrder(cancel);
        System.out.println("cancelId=" + cancelId);
    }

    @Test
    public void testCheckAndFillOrder() {
        List<TradeSpotOrder> allOrder = spotService.fetchAsyncExecuteOrders();
        for (TradeSpotOrder order : allOrder) {
            spotService.checkAndFillOrder(order, false);
        }
    }

    @Test
    public void testAllActiveOrders() {
        List<String> userList = new ArrayList<>();
        userList.add(testUser);
        List<OrderRes> allOrder = spotService.listAllActiveOrders(userList);
        for (OrderRes order : allOrder) {
            System.out.println("allOrder=" + order.toString());
        }
    }

    @Test
    public void testOrderActive() {
        SpotOrderActiveReq req = new SpotOrderActiveReq();
        req.setSymbol(testSymbol);
        req.setPage(1);
        req.setPageSize(10);

        PageResult<SpotOrderInfoRes> resp = spotService.orderActive(req, testUser);
        System.out.println("orderActive=" + resp.toString());
    }

    @Test
    public void testOrderHistory() {
        SpotOrderHistoryReq req = new SpotOrderHistoryReq();
        req.setSymbol(testSymbol);
        req.setStartTime(345678L);
        req.setEndTime(CommonUtils.getNowTime().getTime());
        req.setOrderItem("TIME");
        req.setOrderMode("DESC");
        req.setPage(1);
        req.setPageSize(10);

        PageResult<SpotOrderInfoRes> resp = spotService.orderHistory(req, testUser);
        System.out.println("orderHistory=" + resp.toString());
    }

    public static void main(String[] args) {


        List<TradeSpotOrder>  tradeSpotOrders = new ArrayList<>();

        List<String>  l= Arrays.asList("BTC_USD","USD_BTC","DOGE_USD","ETH_USDT","DOGE_SOL","DOGE_ETC","HT_DOGE");
        for(int i=0;i<20;i++){
            TradeSpotOrder tradeSpotOrder = new TradeSpotOrder();
            tradeSpotOrder.setSymbol(l.get(i%l.size()));
            tradeSpotOrder.setDirection(RandomUtils.nextBoolean()?Direction.BUY.getName():Direction.SELL.getName());
            tradeSpotOrder.setLockAmount(new BigDecimal(RandomUtils.nextInt()/1000));
            tradeSpotOrders.add(tradeSpotOrder);
        }

        List<TradeSpotOrder> list = tradeSpotOrders.stream()
                .sorted(Comparator.comparing(SpotUtil::lockCoin,(l1, l2) -> PriorityCoinWithUSD.coinCompare(l1,l2)).thenComparing(TradeSpotOrder::getLockAmount)).collect(Collectors.toList());

        list.forEach( p->{
            TradeSpotOrder tradeSpotOrder = new TradeSpotOrder();
            tradeSpotOrder.setSymbol(p.getSymbol());
            tradeSpotOrder.setDirection(p.getDirection());
            System.out.println(SpotUtil.lockCoin(tradeSpotOrder)+"-"+p.getDirection()+"-" +p.getSymbol()+"-" +p.getLockAmount());
        });

    }
}
