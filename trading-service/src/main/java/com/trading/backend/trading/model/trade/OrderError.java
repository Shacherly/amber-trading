package com.google.backend.trading.model.trade;

/**
 * @author trading
 * @date 2021/12/1 11:08
 */
public enum OrderError {

	TRADING_SWAP_ORDER_MEMO_TIMEOUT("trading_swap_order_memo_timeout"),
	TRADING_SWAP_ORDER_MEMO_NO_BALANCE("trading_swap_order_memo_no_balance"),
	TRADING_SWAP_ORDER_MEMO_PRICE_OFFSET_OVER("trading_swap_order_memo_price_offset_over"),

	/**
	 * 余额不足，忽略i18n
	 */
	TRADING_SPOT_ORDER_ERROR_FREEZE_ASSET("trading_spot_order_error_freeze_asset"),
	/**
	 * 忽略i18n
	 */
	TRADING_SPOT_ORDER_ERROR_UNFREEZE_ASSET("trading_spot_order_error_unfreeze_asset"),
	/**
	 * 忽略i18n
	 */
	TRADING_SPOT_ORDER_ERROR_CONVERSION_ASSET("trading_spot_order_error_conversion_asset"),
	/**
	 * 忽略i18n
	 */
	TRADING_SPOT_ORDER_ERROR_CREX("trading_spot_order_error_crex"),

	/**
	 * reduce only，忽略i18n
	 */
	TRADING_MARGIN_ORDER_EXCEPTION_REDUCE_ONLY("trading_margin_order_exception_reduce_only"),
	/**
	 * 保证金不足
	 */
	TRADING_MARGIN_ORDER_EXCEPTION_INSUFFICIENT_MARGIN("trading_margin_order_exception_insufficient_margin"),
	/**
	 * 触发总持仓上限
	 */
	TRADING_MARGIN_ORDER_EXCEPTION_EXCEED_TOTAL_POSITION_LIMIT("trading_margin_order_exception_exceed_total_position_limit"),
	/**
	 * 触发单币种持仓上限
	 */
	TRADING_MARGIN_ORDER_EXCEPTION_EXCEED_SYMBOL_POSITION_LIMIT("trading_margin_order_exception_exceed_symbol_position_limit"),
	/**
	 * 忽略i18n
	 */
	TRADING_MARGIN_ORDER_EXCEPTION("trading_margin_order_exception"),
	;

	private String name;

	OrderError(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
