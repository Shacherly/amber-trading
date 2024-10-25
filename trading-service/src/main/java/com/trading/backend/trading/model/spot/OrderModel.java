package com.google.backend.trading.model.spot;

/**
 * @author adam.wang
 * @date 2021/10/5 15:22
 */
public enum OrderModel {
    /**
     * 币对
     */
    SYMBOL("ASC","ASC"),
    /**
     * 时间
     */
    TIME("DESC","DESC");
    private final String code;
    private final String name;

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    OrderModel(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static OrderModel getByCode(String code) {
        for (OrderModel value : OrderModel.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("OrderModel not found by code");
    }
}
