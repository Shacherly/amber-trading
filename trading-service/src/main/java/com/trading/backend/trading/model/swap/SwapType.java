package com.google.backend.trading.model.swap;


/**
 * 兑换类型的枚举值
 *
 * @author trading
 * @date 2021/10/04 09:41
 */
public enum SwapType {

	// 兑换的支付类型
	PAYMENT("PAYMENT", "PAYMENT"),
	OBTAINED("OBTAINED", "OBTAINED"),

	;


	private final String code;
	private final String name;

	SwapType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	public String getName() { return name; }

	public static SwapType getByCode(String code) {
		for (SwapType value : SwapType.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		throw new RuntimeException("SwapType not found by code");
	}

	public static SwapType getByName(String name) {
		for (SwapType value : SwapType.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		throw new RuntimeException("SwapType not found by name");
	}

	public static boolean isPayment(String name) {
		return (name.equals(SwapType.PAYMENT.getName()));
	}

	public static boolean isPayment(Integer mode) {
		return mode.toString().equals(SwapType.PAYMENT.getName());
	}
}
