package com.google.backend.trading.task;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.service.SpotService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 现货的异步检查执行循环
 *
 * @author savion.chen
 * @date 2021/10/22 11:14
 */
@Slf4j
@Component
public class SpotOrderExecuteLoopThread extends AbstractLoopThread {
    @Autowired
    private SpotService spotService;

    public SpotOrderExecuteLoopThread() {
        super(Duration.ofSeconds(5).toMillis());
    }

    @Override
    @Trace(operationName = "SpotOrderExecuteLoopThread")
    public void handle() throws Throwable {
        executeSpotAsyncOrder();
        Thread.sleep(Duration.ofMillis(100).toMillis());
    }

    private boolean executeSpotAsyncOrder() {
        List<TradeSpotOrder> pendList = spotService.fetchAsyncExecuteOrders();
        log.info("loop handler handle execute spot order num = {}", pendList.size());
        for (TradeSpotOrder order : pendList) {
            if (spotService.isSpotSyncOrder(order)) {
                continue;
            }
            try {
                spotService.checkAndFillOrder(order, true);
            } catch (Exception e) {
                log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
            }
        }
        return true;
    }
}
