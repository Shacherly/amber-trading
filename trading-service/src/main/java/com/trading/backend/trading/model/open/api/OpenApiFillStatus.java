package com.google.backend.trading.model.open.api;

import com.google.backend.trading.model.trade.OrderStatus;

public enum OpenApiFillStatus {
    /**
     * 异常
     */
    PENDING,
    /**
     * 执行中
     */
    ING,
    /**
     * 已取消
     */
    CANCELLED,
    /**
     * 已结束
     */
    FINISHED;

    public static OpenApiFillStatus fromOrderStatus(OrderStatus status) {
        switch(status) {
            case PRE_TRIGGER:
                return PENDING;
            case PENDING:
                return PENDING;
            case EXECUTING:
                return ING;
            case LOCKED:
                return ING;
            case EXCEPTION:
                return PENDING;
            case COMPLETED:
                return FINISHED;
            case CANCELED:
                return CANCELLED;
            case PART_CANCELED:
                return CANCELLED;
            default:
                throw new RuntimeException("invalid order status type: {} for open api");
        }
    }

}
