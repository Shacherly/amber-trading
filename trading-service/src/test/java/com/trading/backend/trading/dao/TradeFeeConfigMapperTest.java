package com.google.backend.trading.dao;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.dao.mapper.TradeFeeConfigMapper;
import com.google.backend.trading.dao.model.TradeFeeConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author trading
 * @date 2021/10/12 13:58
 */
@Slf4j
public class TradeFeeConfigMapperTest extends TradingServiceApplicationTest {

	@Autowired
	private TradeFeeConfigMapper tradeFeeConfigMapper;

	@Test
	public void test() {
		TradeFeeConfig config1 = tradeFeeConfigMapper.selectFeeConfigByUid("616289d4d4b1a6d195d6f288");
		log.info("trade fee config1 = {}", config1);
		TradeFeeConfig config2 = tradeFeeConfigMapper.selectFeeConfigByUid("616289d4d4b1a6d195d6f280");
		log.info("trade fee config2 = {}", config2);
		TradeFeeConfig config3 = tradeFeeConfigMapper.selectFeeConfigByUid(null);
		log.info("trade fee config3 = {}", config3);
	}
}
