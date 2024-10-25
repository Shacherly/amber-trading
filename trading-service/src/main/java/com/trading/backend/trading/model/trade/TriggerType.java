package com.google.backend.trading.model.trade;

/**
 * 条件单的触发类型
 *
 * @author savion.chen
 * @date 2021/10/2 16:59
 */
public enum TriggerType {
    // 大于
    GREATER (">", ">"),
    // 小于
    LESS ("<", "<"),
    ;

    private final String code;
    private final String name;

    TriggerType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() { return name; }

    public static TriggerType getByCode(String code) {
        for (TriggerType value : TriggerType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("TriggerType not found by code");
    }

    public static TriggerType getByName(String name) {
        for (TriggerType value : TriggerType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isGreater(TriggerType value) {
        return (value == TriggerType.GREATER);
    }

}
