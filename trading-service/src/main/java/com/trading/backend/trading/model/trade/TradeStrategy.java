package com.google.backend.trading.model.trade;

import org.apache.commons.lang3.StringUtils;

/**
 * @author trading
 * @date 2021/9/27 16:59
 */
public enum TradeStrategy {

	/**
	 * 一致有效至取消，订单将持续有效，直到完全执行或被用户手动取消
	 */
	GTC("GTC", "GTC"),
	/**
	 * 立即成交或取消，订单必须立即以限价或更优的价格成交，如果订单无法立即完全成交，未成交的部分被取消
	 */
	IOC("IOC", "IOC"),
	/**
	 * 全数立即执行，指按现价成交一个订单中的全部的数量；该订单中不能全部成交时，订单全部被取消
	 */
	FOK("FOK", "FOK"),
	;


    private final String code;
	private final String name;

	TradeStrategy(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	public String getName() { return name; }

	public static TradeStrategy getByCode(String code) {
		for (TradeStrategy value : TradeStrategy.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		return null;
	}

	public static TradeStrategy getByName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (TradeStrategy value : TradeStrategy.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		return null;
	}

	public static boolean isGTC(String name){
		return name.equals(TradeStrategy.GTC.getName());
	}
}
