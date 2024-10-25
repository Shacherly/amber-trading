package com.google.backend.trading.model.crex;

/**
 * PDT下单的标识信息
 *
 * @author savion.chen
 * @date 2021/10/4 15:37
 */

public enum CrexNodeEnum {
    // 去PDT下单所支持的备注信息
    GTC("GTC"),
    SPOT("SPOT"),
    LIQUIDATION("LIQUIDATION"),
    PNL_SETTLE("PNL_SETTLE") {};

    private final String name;
    CrexNodeEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
