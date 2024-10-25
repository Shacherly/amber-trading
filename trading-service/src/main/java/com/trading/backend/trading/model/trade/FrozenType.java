package com.google.backend.trading.model.trade;

/**
 * 资金冻结后成交更新的类型
 *
 * @author savion.chen
 * @date 2021/10/11 17:34
 */
public enum FrozenType {

    // 用户下单或系统抵扣， 注意不要大写
    CUSTOMER ("customer", "customer"),
    DEDUCTION ("deduction", "deduction"),
    CONVERSION ("conversion", "conversion"),
    ;

    private final String code;
    private final String name;

    FrozenType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() { return name; }

}
