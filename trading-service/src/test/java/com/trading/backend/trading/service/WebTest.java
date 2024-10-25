package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.model.common.PageReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.web.OrderHistoryReq;
import com.google.backend.trading.model.web.OrderHistoryRes;
import com.google.backend.trading.model.web.OrderInfoRes;
import com.google.backend.trading.model.web.TransactionInfoRes;
import com.google.backend.trading.model.web.TransactionReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author adam.wang
 * @date 2021/10/23 12:10
 */
@Slf4j
public class WebTest extends TradingServiceApplicationTest {

    @Autowired
    private WebService webService;

    @Test
    public void testActive(){
        PageResult<OrderInfoRes> orderInfoResPageResult = webService.orderActive(new PageReq(), "616289a2d4b1a6d195d6f286");
        log.info("testActive {}"+orderInfoResPageResult);
    }

    @Test
    public void testHistory(){
        OrderHistoryReq orderHistoryReq = new OrderHistoryReq();
        orderHistoryReq.setStatus(OrderStatus.CANCELED.getCode());
        PageResult<OrderHistoryRes> orderInfoResPageResult = webService.orderHistory(orderHistoryReq, "616289a2d4b1a6d195d6f286");
        log.info("testHistory {}"+orderInfoResPageResult);
    }

    @Test
    public void testTransaction(){
        TransactionReq orderHistoryReq = new TransactionReq();
        orderHistoryReq.setSymbol("BTC_USD");
        PageResult<TransactionInfoRes> orderInfoResPageResult = webService.transaction(orderHistoryReq, "616289a2d4b1a6d195d6f286");
        log.info("testTransaction {}"+orderInfoResPageResult);
    }
}
