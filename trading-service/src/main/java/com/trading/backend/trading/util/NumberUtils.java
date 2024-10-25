package com.google.backend.trading.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author trading
 * @date 2021/12/22 20:23
 */
public class NumberUtils {

	private static final ThreadLocal<DecimalFormat> DF = ThreadLocal.withInitial(() -> new DecimalFormat("#,###.########"));

	/**
	 * 千分位逗号分割，最多 8 位小数展示，
	 * @param amount
	 * @return
	 */
	public static String thousandthComma(BigDecimal amount) {
		return DF.get().format(amount);
	}
}
