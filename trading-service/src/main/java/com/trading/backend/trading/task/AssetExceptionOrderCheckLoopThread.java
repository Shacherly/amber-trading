package com.google.backend.trading.task;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 订单资金请求异常处理线程
 * - 现货订单
 * - swap订单
 *
 * @author savion.chen
 * @date 2021/10/22 11:14
 */
@Slf4j
@Component
public class AssetExceptionOrderCheckLoopThread extends AbstractLoopThread {
    @Autowired
    private SpotService spotService;
    @Autowired
    private SwapService swapService;

    public AssetExceptionOrderCheckLoopThread() {
        super(Duration.ofSeconds(20).toMillis());
    }

    @Override
    @Trace(operationName = "AssetExceptionOrderCheckLoopThread")
    public void handle() throws Throwable {
        spotAssetExceptionOrder();
        swapAssetExceptionOrder();
        Thread.sleep(Duration.ofSeconds(3).toMillis());
    }

    private void swapAssetExceptionOrder() {
        List<TradeSwapOrder> swapAssetExceptionOrder = swapService.getAssetExceptionOrder();
        swapAssetExceptionOrder.forEach(order -> {
            try {
                swapService.checkAssetAndUpdateStatus(order);
            } catch (Exception e) {
                log.error("订单资金请求异常处理线程异常：{}", ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void spotAssetExceptionOrder() {
        List<TradeSpotOrder> spotAssetExceptionOrder = spotService.getAssetExceptionOrder();
        spotAssetExceptionOrder.forEach(order -> {
            try {
                spotService.checkAssetAndUpdateStatus(order);
            } catch (Exception e) {
                log.error("订单资金请求异常处理线程异常：{}", ExceptionUtils.getStackTrace(e));
            }
        });
    }
}
