package com.google.backend.trading.model.crex;

/**
 * PDT所支持的订单类型
 *
 * @author savion.chen
 * @date 2021/10/4 15:36
 */
public enum CrexTypeEnum {
    // 去PDT下单所支持的订单类型
    MARKET("MARKET"),
    LIMIT_FAK("LIMIT_FAK"),
    LIMIT_FOK("LIMIT_FOK"),
    LIMIT_FILL_REST("LIMIT_FILL_REST"),
    ;

    private final String name;
    CrexTypeEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
