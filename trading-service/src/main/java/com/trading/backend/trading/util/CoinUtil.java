package com.google.backend.trading.util;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.trade.Direction;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/11 20:30
 */
public class CoinUtil {

	public static BigDecimal getBaseCoinSymbolPrice(String coin, Direction direction) {
		if (coin.equals(Constants.BASE_COIN)) {
			return BigDecimal.ONE;
		}
		String symbolName = coin + "_" + Constants.BASE_COIN;
		return SymbolDomain.nonNullGet(symbolName).price(direction);
	}
}
