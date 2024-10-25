package com.google.backend.trading.service;

import com.google.common.collect.ImmutableMap;
import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.sensors.SensorsTrace;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author trading
 * @date 2021/11/29 15:52
 */
@Slf4j
public class SensorsTraceTest extends TradingServiceApplicationTest {


	@Autowired
	private SensorsTrace sensorsTrace;


	@Test
	public void testSensors() {
		sensorsTrace.track("1234567890", "OPEN_POSITION", ImmutableMap.of("uid", "1234567890"));
	}
}
