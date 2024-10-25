package com.google.backend.trading.push;

/**
 * event命名规范 模块名为前缀，单词间用 . 分割
 * 推送给双端（APP & WEB）
 * @author trading
 * @date 2021/10/23 15:40
 */
public enum PushEventEnum {

	/**
	 * swap更新
	 */
	SWAP_ORDER_UPDATE("trading.swap.order.update"),
	/**
	 * 订单创建
	 */
	WEB_ORDER_NEW("trading.web.order.new"),
	/**
	 * 订单更新
	 */
	WEB_ORDER_UPDATE("trading.web.order.update"),
	/**
	 * 交易创建
	 */
	WEB_TRANSACTION_NEW("trading.web.transaction.new"),
	;

	private String code;

	PushEventEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
