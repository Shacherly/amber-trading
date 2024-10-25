package com.google.backend.trading.dao;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.dao.mapper.TradeFundingRateMapper;
import com.google.backend.trading.dao.model.TradeFundingRate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author trading
 * @date 2021/10/12 13:58
 */
@Slf4j
public class TradeFundingRateMapperTest extends TradingServiceApplicationTest {

	@Autowired
	private TradeFundingRateMapper fundingRateMapper;

	@Test
	public void test() {
		List<TradeFundingRate> list = new ArrayList<>();
		TradeFundingRate fundingRate = new TradeFundingRate();
		fundingRate.setCoin("BTC");
		fundingRate.setLend(new BigDecimal("0.03"));
		fundingRate.setBorrow(new BigDecimal("0.05"));
		fundingRate.setTime(new Date(1634025600000L));
		list.add(fundingRate);
		int i = fundingRateMapper.batchInsertIgnoreConflict(list);
		log.info("TradeFundingRate insertIgnoreConflict = {}", i);
		i = fundingRateMapper.batchInsertIgnoreConflict(list);
		log.info("TradeFundingRate insertIgnoreConflict = {}", i);
	}
}
