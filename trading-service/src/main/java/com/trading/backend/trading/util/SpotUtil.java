package com.google.backend.trading.util;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.model.trade.Direction;
import org.springframework.data.util.Pair;

/**
 * @author trading
 * @date 2021/10/13 17:06
 */
public class SpotUtil {


	/**
	 * 获取订单的锁定币种
	 * @param order
	 * @return
	 */
	public static String lockCoin(TradeSpotOrder order) {
		String symbol = order.getSymbol();
		Pair<String, String> pair = CommonUtils.coinPair(symbol);
		String baseCoin = pair.getFirst();
		String quoteCoin = pair.getSecond();

		if (Direction.BUY.getName().equals(order.getDirection())) {
			return quoteCoin;
		} else {
			return baseCoin;
		}
	}
}
