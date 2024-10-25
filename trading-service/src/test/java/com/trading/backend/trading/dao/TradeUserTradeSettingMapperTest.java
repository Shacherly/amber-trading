package com.google.backend.trading.dao;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.dao.mapper.TradeUserTradeSettingMapper;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author trading
 * @date 2021/10/11 17:47
 */
@Slf4j
public class TradeUserTradeSettingMapperTest extends TradingServiceApplicationTest {

	@Autowired
	private TradeUserTradeSettingMapper tradeSettingMapper;

	@Test
	public void test() {
		TradeUserTradeSetting setting = new TradeUserTradeSetting();
		setting.setUid("616289d4d4b1a6d195d6f288");
		setting.setMaxLoss(BigDecimal.ZERO);
		tradeSettingMapper.updateUserTradeSettingByUid(setting);

		List<String> uidList = tradeSettingMapper.selectNeedSettleUidList(Collections.singletonList("(UTC-03:00) Montevideo"), new Date());
		log.info("test");

	}

}
