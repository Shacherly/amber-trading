package com.google.backend.trading.config.feign;

import com.google.backend.trading.client.feign.ErrorLogFeignClient;
import feign.Client;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * @author trading
 * @date 2021/10/23 11:36
 */
public class FeignConfig {

	@Bean
	@Profile("dev")
	Logger.Level feignLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public Client errorLogFeignClient() {
		return new ErrorLogFeignClient(null, null);
	}
}
