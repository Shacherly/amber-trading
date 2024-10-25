package com.google.backend.trading.config;

import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.sensorsdata.analytics.javasdk.consumer.ConcurrentLoggingConsumer;
import com.sensorsdata.analytics.javasdk.consumer.ConsoleConsumer;
import com.google.backend.trading.sensors.SensorsTrace;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author trading
 */
@Slf4j
@Configuration
public class SensorsConfiguration {

	private final TradeProperties.Sensors sensors;

	public SensorsConfiguration(TradeProperties properties) {
		this.sensors = properties.getSensors();
	}

	@Bean
	public SensorsAnalytics sensorsAnalytics() {
		try {
			String logPath = sensors.getLogPath();
			File file = new File(logPath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			return new SensorsAnalytics(new ConcurrentLoggingConsumer(logPath));
		} catch (IOException e) {
			AlarmLogUtil.alarm("SensorsAnalytics IOException, switch to console consumer, cause = {}", ExceptionUtils.getRootCause(e), e);
		}
		return new SensorsAnalytics(new ConsoleConsumer(System.console().writer()));
	}

	@Bean
	public SensorsTrace sensorsTrace() {
		return new SensorsTrace(sensorsAnalytics());
	}
}