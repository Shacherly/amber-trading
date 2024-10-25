package com.google.backend.trading.model.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 订单的状态类型
 *
 * @author savion.chen
 * @date 2021/9/29 18:29
 */
public enum OrderStatus {
    /**
     * 待触发
     */
    PRE_TRIGGER("PRE_TRIGGER", "PRE_TRIGGER"),
    /**
     * 等待处理，调用资金判定的中间状态
     */
    PENDING("PRE_TRIGGER", "PENDING"),
    /**
     * PDT 发单 timeout
     * （最多持续60S）
     */
    PDT_PENDING("PRE_TRIGGER", "PDT_PENDING"),
    /**
     * 资金请求 timeout，且同步单才会有此状态
     * 此时展示为(CANCELED)已取消，另起线程调用资金判定
     * （最多持续60S）
     */
    ASSET_PENDING("CANCELED", "ASSET_PENDING"),
    /**
     * 挂单中
     */
    EXECUTING("EXECUTING", "EXECUTING"),
    /**
     * 订单发单中
     */
    LOCKED("EXECUTING", "LOCKED"),
    /**
     * AIP(定投)订单发单中
     */
    AIP_LOCKED("EXECUTING", "AIP_LOCKED"),
    /**
     * 执行异常，（保证金不足，持仓上限等，具体原因会记录到error字段） margin单独有
     */
    EXCEPTION("EXCEPTION", "EXCEPTION"),
    /**
     * 取消中
     */
    CANCELING("CANCELING", "CANCELING"),

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
    PART_CANCELED("CANCELED", "PART_CANCELED"),
    ;


    /**
     * 外部展示
     */
    private final String code;
    /**
     * DB状态码
     */
    private final String name;

    OrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() { return name; }

    public static List<OrderStatus> getByCode(String code) {
        List<OrderStatus> list = new ArrayList<>();
        for (OrderStatus value : OrderStatus.values()) {
            if (value.getCode().equals(code)) {
                list.add(value);
            }
        }
        return list;
    }

    public static OrderStatus getByName(String name) {
        for (OrderStatus value : OrderStatus.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new RuntimeException("OrderStatus not found by name");
    }

    public static String getInitStatus(boolean isTrigger) {
        if (isTrigger) {
            return OrderStatus.PRE_TRIGGER.getName();
        } else {
            return OrderStatus.PENDING.getName();
        }
    }


    public static boolean isInitStatus(String name) {
        OrderStatus target = OrderStatus.getByName(name);
        return (target == OrderStatus.PENDING || target == OrderStatus.PRE_TRIGGER);
    }

    public static boolean isFinish(String name) {
        OrderStatus target = OrderStatus.getByName(name);
        return (target == OrderStatus.COMPLETED || target == OrderStatus.CANCELED
                || target == OrderStatus.PART_CANCELED);
    }

    public static boolean canCancelSpot(String name) {
        return CAN_CANCEL_SPOT_STATUS.contains(name);
    }

    public static boolean haveTraded(String name) {
        OrderStatus target = OrderStatus.getByName(name);
        return (target == OrderStatus.COMPLETED || target == OrderStatus.PART_CANCELED);
    }

    public static OrderStatus getFeignStatus(String state) {
        return getByName(state);
    }

    public boolean isFinish() {
        return (this == OrderStatus.COMPLETED || this == OrderStatus.CANCELED
                || this == OrderStatus.PART_CANCELED);
    }

    /**
     * 活跃状态
     */
    public static final List<String> ACTIVE_STATUS = Arrays.asList(OrderStatus.PRE_TRIGGER.getName(), OrderStatus.PENDING.getName(),
            OrderStatus.EXECUTING.getName(), OrderStatus.LOCKED.getName(), OrderStatus.EXCEPTION.getName(),
            PDT_PENDING.getName());

    /**
     * 处于执行中的订单
     */
    public static final List<String> EXECUTE_STATUS = Arrays.asList(OrderStatus.PRE_TRIGGER.getName(),
            OrderStatus.PENDING.getName(), OrderStatus.EXECUTING.getName());


    /**
     * 历史状态
     */
    public static final List<String> HISTORY_STATUS = Arrays.asList(OrderStatus.CANCELED.getName(),
            OrderStatus.PART_CANCELED.getName(), OrderStatus.COMPLETED.getName(), OrderStatus.ASSET_PENDING.getName());

    public static final List<String> CAN_CANCEL_SPOT_STATUS = Arrays.asList(OrderStatus.PRE_TRIGGER.getName(),
            OrderStatus.EXECUTING.getName());

    /**
     * APP 查询状态
     * COMPLETED(全部成交),CANCELED(取消)
     */
    public static final String APP_QUERY_STATUS_CANCELED = "CANCELED";
    public static final String APP_QUERY_STATUS_COMPLETED = "COMPLETED";
    public static final List<String> APP_COMPLETED_STATUS = Arrays.asList(OrderStatus.COMPLETED.getName());
    public static final List<String> APP_CANCELED_STATUS = Arrays.asList(OrderStatus.CANCELED.getName(), OrderStatus.ASSET_PENDING.getName(), OrderStatus.PART_CANCELED.getName());
}
