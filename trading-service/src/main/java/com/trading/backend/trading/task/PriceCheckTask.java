package com.google.backend.trading.task;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.trace.annotation.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author trading
 * @date 2021/12/3 17:52
 */
@Slf4j
@Component
public class PriceCheckTask {

	@TraceId
	@Scheduled(fixedRate = Constants.REFRESH_PRICE_CHECK_INTERVAL_MILL, initialDelay = 5 * 60 * 1000)
	public void priceCheck() {
		log.info("handle priceCheck");
		Collection<SymbolDomain> values = SymbolDomain.CACHE.values();
		for (SymbolDomain value : values) {
			value.checkPlaceOrderPriceStatus();
		}

	}
}
