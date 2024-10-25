package com.google.backend.trading.model.user;

import java.math.BigDecimal;

/**
 * 条件单的触发类型
 *
 * @author savion.chen
 * @date 2021/10/2 16:59
 */
public enum AlarmTriggerType {
    // 大于>
    GREATER(">", ">"),
    // <小于
    LESS("<", "<"),
    ;

    private final String code;
    private final String name;

    AlarmTriggerType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AlarmTriggerType getByCode(String code) {
        for (AlarmTriggerType value : AlarmTriggerType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("AlarmTriggerType not found by code");
    }

    public static AlarmTriggerType getByName(String name) {
        for (AlarmTriggerType value : AlarmTriggerType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new RuntimeException("AlarmTriggerType not found by name");
    }

    public static boolean isGreater(AlarmTriggerType value) {
        return (value == AlarmTriggerType.GREATER);
    }

    /**
     * 反转符号
     * @param value
     * @return
     */
    public static AlarmTriggerType reversal(AlarmTriggerType value) {
        if (value == AlarmTriggerType.GREATER) {
            return AlarmTriggerType.LESS;
        } else {
            return AlarmTriggerType.GREATER;
        }
    }

    /**
     * 是否满足alarm条件
     * @param alarmPrice
     * @param compare
     * @param compPrice
     * @return
     */
    public static boolean isSatisfyAlarm(BigDecimal alarmPrice, AlarmTriggerType compare, BigDecimal compPrice) {
        if (compare == AlarmTriggerType.GREATER) {
            return compPrice.compareTo(alarmPrice) >= 0;
        }else if (compare == AlarmTriggerType.LESS) {
            return compPrice.compareTo(alarmPrice) <= 0;
        }
        return false;
    }

}
