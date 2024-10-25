package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author trading
 * @date 2021/10/19 19:43
 */
public class TradeAssetServiceTest extends TradingServiceApplicationTest {

	@Autowired
	private MarginService marginService;

	@Test
	public void testMarginInfo() {
		marginService.marginInfo("616289a2d4b1a6d195d6f286");
	}

}
