package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.Position6HFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * 现货接口的单元测试
 *
 * @author savion.chen
 * @date 2021/10/10 16:50
 */
public class MarginServiceTest extends TradingServiceApplicationTest {

    @Autowired
    private MarginService marginService;
    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private OrderRequest orderRequest;
    private final static String testSymbol = "ETH_USD";
    private final static String testUser = "616289a2d4b1a6d195d6f286";

    @Test
    public void testPlaceOrderFok() {
        MarginOrderPlace req = new MarginOrderPlace();
        req.setSymbol(testSymbol);
        req.setUid(testUser);
        req.setType(OrderType.LIMIT);
        req.setStrategy(TradeStrategy.FOK);
        req.setDirection(Direction.BUY);
        req.setQuantity(new BigDecimal("1"));
        req.setPrice(new BigDecimal("4000"));
        req.setNotes("HELLO");
        req.setSource(SourceType.PLACED_BY_CLIENT);
        MarginOrderInfoRes res = marginService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res);
        }
    }

    @Test
    public void testPlaceOrderMarket() {
        MarginOrderPlace req = new MarginOrderPlace();
        req.setSymbol(testSymbol);
        req.setUid(testUser);
        req.setType(OrderType.MARKET);
        req.setDirection(Direction.BUY);
        req.setQuantity(new BigDecimal("1"));
        req.setPrice(new BigDecimal("4000"));
        req.setNotes("HELLO");
        req.setSource(SourceType.FORCE_CLOSE);
        MarginOrderInfoRes res = marginService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res);
        }
    }

    @Test
    public void getActivePosition() {
        List<ActivePositionInfoVo> res = marginService.positionActive(testUser);

        if (res != null) {
            System.out.println("result=" + res);
        }
    }

    @Test
    public void getSettlePositionInfo() {
        PositionSettleInfoRes res = marginService.getSettlePositionInfo("ea60379c-fcf5-4f41-b45a-13e689fcc297", testUser);
        if (res != null) {
            System.out.println("result=" + res);
        }
    }

    @Test
    public void settlePosition() {
        PositionSettle req = new PositionSettle(testUser, "ea60379c-fcf5-4f41-b45a-13e689fcc297", new BigDecimal("1"),
                SourceType.PLACED_BY_CLIENT, false);
        marginService.settlePosition(req);
    }

    @Test
    public void autoSettlePosition() {
        marginService.autoSettlePosition(testUser);
    }

    @Test
    public void marginAssetInfo() {
        MarginAssetInfoRes res = marginService.marginAssetInfo("617a0ca4e562b2db2889b9b7");
        System.out.println("result=" + res);
    }

    @Test
    public void getFundingCostBefore6H() {
        Position6HFundingCostVo fundingCostBefore6H = marginService.getFundingCostBefore6H(System.currentTimeMillis());
        System.out.println(fundingCostBefore6H);
    }
}
