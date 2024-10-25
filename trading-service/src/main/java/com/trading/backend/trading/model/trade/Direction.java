package com.google.backend.trading.model.trade;

/**
 * @author trading
 * @date 2021/9/29 19:17
 */
public enum Direction {

	/**
	 * 用户买卖方向
	 */

	BUY("BUY", "BUY"),
	SELL("SELL", "SELL"),
	;


	private final String code;
	private final String name;

	Direction(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	public String getName() { return name; }


	public static Direction getByCode(String code) {
		for (Direction value : Direction.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		throw new RuntimeException("Direction not found by code");
	}

	public static Direction getByName(String name) {
		for (Direction value : Direction.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		throw new RuntimeException("Direction not found by name");
	}

	public static boolean isBuy(String name) {
		Direction side = Direction.getByName(name);
		return side == Direction.BUY;
	}

	/**
	 * 获取交易反方向
	 * @param direction
	 * @return
	 */
	public static Direction rivalDirection(String direction){
		return Direction.isBuy(direction)?Direction.SELL:Direction.BUY;
	}

}
