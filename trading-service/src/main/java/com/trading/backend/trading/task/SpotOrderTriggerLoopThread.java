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
 * 现货条件单的检查触发循环
 *
 * @author savion.chen
 * @date 2021/10/22 11:11
 */
@Slf4j
@Component
public class SpotOrderTriggerLoopThread extends AbstractLoopThread {
    @Autowired
    private SpotService spotService;

    public SpotOrderTriggerLoopThread() {
        super(Duration.ofSeconds(5).toMillis());
    }

    @Override
    @Trace(operationName = "SpotOrderTriggerLoopThread")
    public void handle() throws Throwable {
        executeSpotTriggerOrder();
        Thread.sleep(Duration.ofMillis(100).toMillis());
    }

    private boolean executeSpotTriggerOrder() {
        List<TradeSpotOrder> pendList = spotService.listAllActiveTriggerOrders();
        log.info("loop handler handle trigger spot order num = {}", pendList.size());
        for (TradeSpotOrder order : pendList) {
            try {
                spotService.checkTriggerOrder(order);
            } catch (Exception e) {
                log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
            }
        }
        return true;
    }

}
