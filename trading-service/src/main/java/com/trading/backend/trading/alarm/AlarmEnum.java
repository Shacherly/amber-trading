package com.google.backend.trading.alarm;

/**
 * 告警按照 code + name 作为组合进行
 *
 * @author trading
 * @date 2021/12/8 16:58
 */
public enum AlarmEnum {

	/**
	 * 现货下单错误（非正常的业务错误）
	 */
	SPOT_PLACE_ORDER_ERROR("spot place order error", "[google-trading] spot place order error (-> crex)"),
	/**
	 * 杠杆下单错误（非正常的业务错误）
	 */
	MARGIN_PLACE_ORDER_ERROR("margin place order error", "[google-trading] margin place order error (-> crex)"),
	/**
	 * 兑换下单错误（非正常的业务错误）
	 */
	SWAP_PLACE_ORDER_ERROR("swap place order error", "[google-trading] swap place order error (-> crex)"),
	/**
	 * 兑换询价错误（非正常的业务错误）
	 */
	SWAP_PRICE_ERROR("swap ask price error", "[google-trading] swap ask price error (-> crex)"),

	/**
	 * 预期外的错误
	 */
	UNEXPECTED_ERROR("unexpected error", "[google-trading] inner unexpected error"),
	;

	public static final String PROD_CODE = "624000";

	public static final String UAT_CODE = "624001";

	private final String name;

	private final String msg;

	AlarmEnum(String name, String msg) {
		this.name = name;
		this.msg = msg;
	}

	public String getName() {
		return name;
	}

	public String getMsg() {
		return msg;
	}
}
