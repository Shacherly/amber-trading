package com.google.backend.trading;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author trading
 * @date 2021/9/27 13:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class TradingServiceApplicationTest {

	@BeforeClass
	public static void initSystemProperty() {
		System.setProperty("trading.loop.start", "false");
	}
}