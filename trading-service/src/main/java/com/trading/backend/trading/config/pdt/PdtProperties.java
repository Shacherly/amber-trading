package com.google.backend.trading.config.pdt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author trading
 * @date 2021/10/8 20:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "pdt")
public class PdtProperties {

	private RedisConfig redis;

	private Server server;

	@Data
	public static class RedisConfig {

		private String host;

		private Integer port;

		private String password;

		private Integer timeout;

		private Integer database;

		private boolean ssl = true;
	}

	@Data
	public static class Server {

		private String host;
	}

}
