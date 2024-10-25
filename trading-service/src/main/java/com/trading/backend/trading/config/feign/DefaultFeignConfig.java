package com.google.backend.trading.config.feign;

import feign.Contract;
import org.springframework.context.annotation.Bean;

/**
 * @author trading
 * @date 2021/10/23 11:36
 */
public class DefaultFeignConfig {

	@Bean
	public Contract feignContract() {
		return new Contract.Default();
	}
}
