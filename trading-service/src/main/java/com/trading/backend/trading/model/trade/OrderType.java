package com.google.backend.trading.model.trade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 现货订单类型
 *
 * @author trading
 * @date 2021/9/27 16:47
 */
public enum OrderType {

	/**
	 * 限价单
	 */
	LIMIT("LIMIT", "LIMIT"),
	/**
	 * 市价单
	 */
	MARKET("MARKET", "MARKET"),
	/**
	 * 条件限价单
	 */
	STOP_LIMIT("STOP_LIMIT", "STOP_LIMIT"),
	/**
	 * 条件市价单
	 */
	STOP_MARKET("STOP_MARKET", "STOP_MARKET"),
	;


	private final String code;
	private final String name;

	OrderType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static List<String> getListByNameLike(String name) {
		return Stream.of(OrderType.values()).map(OrderType::getName).filter(valueName -> valueName.contains(name)).collect(Collectors.toList());
	}

	public static String getSimpleByName(OrderType orderType) {
		if (orderType == OrderType.STOP_LIMIT) {
			return "LIMIT";
		} else if (orderType == OrderType.STOP_MARKET) {
			return "MARKET";
		} else if (orderType == OrderType.LIMIT) {
			return "LIMIT";
		} else if (orderType == OrderType.MARKET) {
			return "MARKET";
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static boolean isTriggerOrder(String name) {
		OrderType target = OrderType.getByName(name);
		return (target == OrderType.STOP_LIMIT || target == OrderType.STOP_MARKET);
	}

	public static boolean isLimitOrder(String name) {
		OrderType target = OrderType.getByName(name);
		return (target == OrderType.LIMIT || target == OrderType.STOP_LIMIT);
	}

	public static OrderType getByCode(String code) {
		for (OrderType value : OrderType.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		throw new RuntimeException("OrderType not found by code");
	}

	public static OrderType getByName(String name) {
		for (OrderType value : OrderType.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		throw new RuntimeException("OrderType not found by name");
	}

	public boolean isTriggerOrder() {
		return (this == OrderType.STOP_LIMIT || this == OrderType.STOP_MARKET);
	}

	public boolean isLimitOrder() {
		return (this == OrderType.LIMIT || this == OrderType.STOP_LIMIT);
	}


	public static final List<String> TRIGGER_TYPE = Arrays.asList(
			OrderType.STOP_LIMIT.getName(), OrderType.STOP_MARKET.getName());
}
