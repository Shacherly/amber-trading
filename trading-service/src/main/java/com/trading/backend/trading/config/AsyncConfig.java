package com.google.backend.trading.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 线程池的配置
 * @author adam.wang
 * @date 2021/10/14 10:59
 */
@Component
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private static final int MAX_POOL_SIZE = 16;

    private static final int CORE_POOL_SIZE = 16;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setTaskDecorator(new MdcTaskDecorator());
        asyncTaskExecutor.setThreadNamePrefix("async-pool-");
        asyncTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> log.error("asyc execute error, method={}, params={}", method.getName(),
                Arrays.toString(params), ExceptionUtils.getRootCauseMessage(throwable), throwable);
    }

    public static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }
}