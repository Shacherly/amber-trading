package com.google.backend.trading.model.trade;

/**
 * @author trading
 * @date 2021/10/13 16:31
 */
public enum TradeTerminator {

	/**
	 * 客户中止行为
	 */
	CLIENT("CLIENT"),
	/**
	 * 系统中止：保证金不够，超过最大持仓限额，只减仓，同步单未完全成交
	 */
	SYSTEM("SYSTEM"),
	/**
	 * 风控系统中止行为
	 */
	RISK("RISK"),
	;

	private final String code;

	TradeTerminator(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static TradeTerminator getByCode(String code) {
		for (TradeTerminator value : TradeTerminator.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		throw new RuntimeException(String.format("TradeTerminator not found by code, code = %s", code));
	}

	public boolean isFromUser() {
		return this == TradeTerminator.CLIENT;
	}

}
