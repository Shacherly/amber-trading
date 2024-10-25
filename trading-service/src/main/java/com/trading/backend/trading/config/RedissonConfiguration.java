package com.google.backend.trading.config;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author trading
 * @date 2021/12/8 15:32
 */
@Configuration
public class RedissonConfiguration {

	@Bean
	public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
		//使用jackson做cache序列化
		return configuration -> configuration.setCodec(new JsonJacksonCodec());
	}
}
