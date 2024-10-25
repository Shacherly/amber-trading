package com.google.backend.trading.model.trade;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单的状态类型
 *
 * @author savion.chen
 * @date 2021/9/29 18:29
 */
public enum OpenApiOrderStatus {
    /**
     * 待触发
     */
    PRE_TRIGGER("PRE_TRIGGER", "PRE_TRIGGER"),
    /**
     * 等待处理，调用资金判定的中间状态
     */
    PENDING("PRE_TRIGGER", "PENDING"),
    /**
     * 挂单中
     */
    EXECUTING("EXECUTING", "EXECUTING"),
    /**
     * 订单发单中
     */
    LOCKED("EXECUTING", "LOCKED"),
    /**
     * 执行异常，（保证金不足，持仓上限等，具体原因会记录到error字段） margin单独有
     */
    EXCEPTION("EXCEPTION", "EXCEPTION"),

    /**
     * 完全成交
     */
    COMPLETED("COMPLETED", "COMPLETED"),
    /**
     * 完全取消
     */
    CANCELED("CANCELED", "CANCELED"),
    /**
     * 部分成交取消
     */
    PART_CANCELED("PART_CANCELED", "PART_CANCELED"),
    ;


    private final String code;
    private final String name;

    OpenApiOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static List<OpenApiOrderStatus> getByCode(String code) {
        List<OpenApiOrderStatus> list = new ArrayList<>();
        for (OpenApiOrderStatus value : OpenApiOrderStatus.values()) {
            if (value.getCode().equals(code)) {
                list.add(value);
            }
        }
        return list;
    }

    public static OpenApiOrderStatus getByName(String name) {
        for (OpenApiOrderStatus value : OpenApiOrderStatus.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new RuntimeException("OrderStatus not found by name");
    }

    public static OpenApiOrderStatus getFeignStatus(String state) {
        return getByName(state);
    }
}
