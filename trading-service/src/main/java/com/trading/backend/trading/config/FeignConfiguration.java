package com.google.backend.trading.config;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author trading
 * @date 2021/3/23 17:09
 */
@Slf4j
@Configuration
@EnableCircuitBreaker
@EnableFeignClients(basePackages = "com.google.backend.trading.client")
public class FeignConfiguration {

	/**
	 * 使用调用hystrix feign时日志MDC上下文传递，用于跨线程传递trace id
	 */
	public static class MdcHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

		@Override
		public <T> Callable<T> wrapCallable(Callable<T> callable) {
			Map<String, String> contextMap = MDC.getCopyOfContextMap();
			return () -> {
				try {
					MDC.setContextMap(contextMap);
					return callable.call();
				} finally {
					MDC.clear();
				}
			};
		}
	}

}
