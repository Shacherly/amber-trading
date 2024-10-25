package com.google.backend.trading.task;

import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * AIP定投 兑换的异步检查执行循环
 *
 * @author savion.chen
 * @date 2021/10/22 11:15
 */
@Slf4j
@Component
public class AipSwapOrderExecuteLoopThread extends AbstractLoopThread {
    @Autowired
    private SwapService swapService;

    public AipSwapOrderExecuteLoopThread() {
        super(Duration.ofSeconds(15).toMillis());
    }


    @Override
    @Trace(operationName = "AipSwapOrderExecuteLoopThread")
    public void handle() throws Throwable {
        executeAipSwapOrder();
        Thread.sleep(Duration.ofSeconds(10).toMillis());
    }

    private boolean executeAipSwapOrder() {
        List<TradeSwapOrder> pendList = swapService.listAllAipActiveOrders();
        log.info("loop handler handle execute aip swap order num = {}", pendList.size());
        for (TradeSwapOrder order : pendList) {
            try {
                swapService.aipPerformOrder(order);
            } catch (Exception e) {
                AlarmLogUtil.alarm("executeAipSwapOrder err, order = {},cause = {}", order, ExceptionUtils.getRootCauseMessage(e), e);
            }
        }
        return true;
    }
}
