package com.google.backend.trading.service;

import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.controller.SwapController;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.pdt.CreateSwapReq;
import com.google.backend.trading.model.pdt.CreateSwapRes;
import com.google.backend.trading.model.pdt.CrexSwapPriceReq;
import com.google.backend.trading.model.pdt.SwapByIdRes;
import com.google.backend.trading.model.swap.api.CoinBalanceRes;
import com.google.backend.trading.model.swap.api.SwapNotice;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryReq;
import com.google.backend.trading.model.swap.api.SwapOrderPlaceReq;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.model.trade.FrozenType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.util.CommonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;


/**
 * 自测Swap接口
 *
 * @author savion.chen
 * @date 2021/10/8 21:06
 */
public class SwapServiceTest extends TradingServiceApplicationTest {

    @Autowired
    private SwapService swapService;
    @Autowired
    private SwapController swapController;
    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private CrexApiRequest pdtRequest;
    @Autowired
    private PushComponent pushComponent;

    private final static String testUser = "61628983d4b1a6d195d6f285";
    //private final  static  String testUser = "60b08e48c64cb15040ffdb69";
    private final static String orderId = "aaabbbcc1111";

    private UserInfo getTestUser() {
        UserInfo user = new UserInfo();
        user.setUid(testUser);
        user.setKycStatus(1);
        return user;
    }

    private BigDecimal getSwapPrice(String from, String to, String mode) {
        SwapPriceReq req = new SwapPriceReq();
        req.setMode(mode);
        req.setFromCoin(from);
        req.setToCoin(to);
        BigDecimal qty = new BigDecimal("12");
        req.setQuantity(qty);
        req.setFeeFree(false);

        BigDecimal placePrice = null;
        Response<SwapPriceRes> resp1 = swapController.price(req, getTestUser());
        if (resp1.getCode() == 0) {
            SwapPriceRes node = resp1.getData();
            placePrice = CommonUtils.convertShow(node.getPrice());
            System.out.println("getPrice=" + placePrice + " getReversePrice=" + node.getReversePrice());
        } else {
            System.out.println("queryPrice code=" + resp1.getCode() + " msg=" + resp1.getMsg());
        }
        return placePrice;
    }

    @Test
    public void testQueryPrice() {
        BigDecimal getPrice = getSwapPrice("ETH", "USD", "PAYMENT");
        System.out.println("getPrice=" + getPrice );
    }

    @Test
    public void testBasePrice() {
        CrexSwapPriceReq req = new CrexSwapPriceReq();
        req.setFrom("BTC");
        req.setTo("USD");
        //req.setQuantity(new BigDecimal("1"));
        req.setToQuantity(new BigDecimal("1"));
        BigDecimal lastPrice = pdtRequest.querySwapPrice(req);
        System.out.println("lastPrice=" + lastPrice);
    }

    @Test
    public void testQueryCoinBalance() {
        List<CoinBalanceRes> respList = assetRequest.querySwapCoinBalance(testUser, null, false);
        if (respList != null) {
            for(CoinBalanceRes node : respList) {
                System.out.println("coin=" + node.getCoin() + " balance=" + node.getBalance());
            }
        } else {
            System.out.println("queryCoinBalance is null");
        }
    }

    @Test
    public void testFreezeFunds(){
        TradeSpotOrderReq sendReq = new TradeSpotOrderReq();
        sendReq.setReqId(RandomTestData.getStrInt(100000, 100000000));

        TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
        param.setUid(testUser);
        param.setCoin("USD");
        param.setCompetitorCoin("ETH");
        param.setAmount(new BigDecimal("211"));
        sendReq.setParams(param);

        BusinessExceptionEnum code = assetRequest.freezeFunds(sendReq);
        System.out.println("freezeFunds isOk=" + code.getMsg());
    }

    @Test
    public void testConversion() {
        TradeCurrencyConversion sendReq = new TradeCurrencyConversion();
        sendReq.setReqId(RandomTestData.getStrInt(100000, 100000000));

        TradeCurrencyConversion.Params params = new TradeCurrencyConversion.Params();
        params.setUid(testUser);
        params.setFeeCoin("USD");
        params.setFee(new BigDecimal("0"));
        params.setDirection("buy");
        params.setTrigger(FrozenType.DEDUCTION.getName());

        params.setBaseCoin("ETH");
        params.setBaseAmount(new BigDecimal("0.1"));
        params.setQuoteCoin("USD");
        params.setQuoteAmount(new BigDecimal("400"));
        sendReq.setParams(params);

        int retCode = assetRequest.conversionCoin(sendReq);
        System.out.println("freezeFunds retCode=" + retCode);
    }

    @Test
    public void testCancelFreeze(){
        TradeSpotOrderReq sendReq = new TradeSpotOrderReq();
        sendReq.setReqId(RandomTestData.getStrInt(100000, 100000000));

        TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
        param.setUid(testUser);
        param.setCoin("USD");
        param.setCompetitorCoin("ETH");
        param.setAmount(new BigDecimal("171.667"));
        sendReq.setParams(param);

        int retCode = assetRequest.cancelFreeze(sendReq);
        System.out.println("cancelFreeze retCode=" + retCode);
    }

    @Test
    public void testUpdateTradedResult(){
        TradeSpotReq tradeReq = new TradeSpotReq();
        tradeReq.setReqId(RandomTestData.getStrInt(100000, 100000000));
        TradeSpotReq.Params params = new TradeSpotReq.Params();
        params.setUid(testUser);
        params.setDirection("buy");
        params.setBaseCoin("ETH");
        params.setBaseAmount(new BigDecimal("0.042"));
        params.setQuoteCoin("USD");
        params.setQuoteAmount(new BigDecimal("156"));

        params.setUnlockAmount(new BigDecimal("0"));
        params.setFee(new BigDecimal("0.245"));
        params.setFeeCoin(Constants.DEFAULT_COIN);
        tradeReq.setParams(params);

        int retCode = assetRequest.updateTradedResult(tradeReq);
        System.out.println("updateTradedResult retCode=" + retCode);
    }


    @Test
    public void testPdtPlaceOrder() {
        BigDecimal placePrice = getSwapPrice("USD", "BTC", "PAYMENT");

        CreateSwapReq swapReq = new CreateSwapReq();
        swapReq.setTradeId(RandomTestData.getStrInt(100000, 100000000));
        swapReq.setFrom("USD");
        swapReq.setTo("BTC");
        swapReq.setPrice(placePrice);
        swapReq.setQuantity(new BigDecimal("200"));
        CreateSwapRes resp = pdtRequest.executeSwapOrder(swapReq);
        if (resp != null) {
            System.out.println("data=" + resp.toString());
        } else {
            System.out.println("executeSwapOrder error");
        }
    }

    @Test
    public void testPdtQuerySwapOrder() {
        String tradeId = "501f8d75-790e-4704-b296-fccbb83bab0f";
        SwapByIdRes resp = pdtRequest.querySwapOrder(tradeId);
        if (resp != null) {
            System.out.println("data=" + resp.toString());
        }
    }

    @Test
    public void testPlaceOrder() {
        BigDecimal placePrice = getSwapPrice("ETH", "USD", "PAYMENT");
        SwapOrderPlaceReq req = new SwapOrderPlaceReq();
        req.setMode("PAYMENT");
        req.setFromCoin("ETH");
        req.setToCoin("USD");
        BigDecimal qty = new BigDecimal("0.15");
        req.setQuantity(qty);
        req.setPrice(placePrice);

        Response<SwapOrderRes> resp = swapController.orderPlace(req, getTestUser());
        System.out.println("placeOrder=" + resp.getData().toString());
    }

    @Test
    public void testQueryLast() {
        SwapOrderRes resp = swapService.queryLast(testUser);
        System.out.println("queryLast=" + resp.toString());
    }

    @Test
    public void testQuerySwapOrder() {
        String orderId = "86dba217-7769-4f23-9613-22861cf34a7d";
        SwapOrderRes resp = swapService.querySwapOrder(orderId, testUser);
        System.out.println("querySwapOrder=" + resp.toString());
    }

    @Test
    public void testQueryHistory() {
        SwapOrderHistoryReq req = new SwapOrderHistoryReq();
        req.setCoin("USD");
//        req.setUid(testUser);
        req.setStartTime(12345L);
        req.setEndTime(CommonUtils.getNowTime().getTime());
        req.setPage(1);
        req.setPageSize(10);
        PageResult<SwapOrderRes> respList = swapService.queryHistory(req, testUser);
        System.out.println("queryHistory=" + respList);
    }

    @Test
    public void testNoticeMessage() throws InterruptedException {
        SwapNotice msg = new SwapNotice();
        msg.setOrder("5521fba9-d28d-4de5-b835-ba47f0d2b2fc");
        msg.setStatus("COMPLETED");
        String testUid = "616652a1f4fc03824883f80e";

        for (int i = 0; i < 100; i++) {
            String sendMsg = "This is test. count=" + i;
            msg.setInfo(sendMsg);
            WsPushMessage<SwapNotice> sendData = WsPushMessage.buildAllConsumersMessage(
                    testUid, PushEventEnum.SWAP_ORDER_UPDATE, msg);
            pushComponent.pushWsMessage(sendData);
            System.out.println("sendMsg= " + sendMsg);
            Thread.sleep(5 * 1000);
        }
    }


}
