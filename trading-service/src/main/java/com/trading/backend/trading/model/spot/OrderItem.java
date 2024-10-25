package com.google.backend.trading.model.spot;

/**
 * @author adam.wang
 * @date 2021/10/5 15:15
 */
public enum OrderItem {
    /**
     * 币对
     */
    SYMBOL("SYMBOL","SYMBOL"),
    /**
     * 时间
     */
    TIME("TIME","TIME");

    private final String code;
    private final String name;

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    OrderItem(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static OrderItem getByCode(String code) {
        for (OrderItem value : OrderItem.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("OrderItem not found by code");
    }

}
