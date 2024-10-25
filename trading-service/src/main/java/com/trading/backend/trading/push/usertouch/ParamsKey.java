package com.google.backend.trading.push.usertouch;

/**
 * 用户敏感信息参数
 *
 * @author savion.chen
 * @date 2021/11/12 17:40
 */
public enum ParamsKey {

    // 参数类型
    CRYPTOCURRENCY("cryptocurrency"),
    CURRENCY_PAIRS("currency_pairs"),

    DIRECTION("direction"),
    ORDER_TYPE("order_type"),

    TRIGGER_PRICE("trigger_price"),
    LIMIT_PRICE("limit_price"),
    FILLED_PRICE("filled_price"),
    AMOUNT("amount"),

    ACCOUNT("account"),
    REALIZED_PNL("realized_P_L"),
    RISK_WARNING("risk_warning"),
    FUNDING_FEE("funding_fee"),

    CURRENCY_PAIRS_SUCCEED("currency_pairs_succeed"),
    CURRENCY_PAIRS_FAILED("currency_pairs_failed"),
    ;

    private String key;

    ParamsKey(String key) {
        this.key = key;
    }

    public String getName() {
        return key;
    }

};
