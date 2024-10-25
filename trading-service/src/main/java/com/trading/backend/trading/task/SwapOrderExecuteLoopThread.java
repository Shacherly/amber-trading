package com.google.backend.trading.task;

import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.service.SwapService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 兑换的异步检查执行循环
 *
 * @author savion.chen
 * @date 2021/10/22 11:15
 */
@Slf4j
@Component
public class SwapOrderExecuteLoopThread extends AbstractLoopThread{
    @Autowired
    private SwapService swapService;

    public SwapOrderExecuteLoopThread() {
        super(Duration.ofSeconds(10).toMillis());
    }


    @Override
    @Trace(operationName = "SwapOrderExecuteLoopThread")
    public void handle() throws Throwable {
        executeSwapOrder();
        Thread.sleep(Duration.ofSeconds(5).toMillis());
    }

    private boolean executeSwapOrder() {
        List<TradeSwapOrder> pendList = swapService.listAllActiveOrders();
        log.info("loop handler handle execute swap order num = {}", pendList.size());
        for (TradeSwapOrder order: pendList) {
            try {
                swapService.checkAndFillOrder(order, true);
            } catch (Exception e) {
                log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
            }
        }
        return true;
    }
}
