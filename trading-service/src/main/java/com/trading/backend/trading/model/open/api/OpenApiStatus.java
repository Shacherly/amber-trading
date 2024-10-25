package com.google.backend.trading.model.open.api;

import com.google.backend.trading.model.trade.OrderStatus;

public enum OpenApiStatus {
    /**
     * 待触发
     */
    PENDING,
    /**
     * 执行中
     */
    NEW,
    /**
     * 部分成交
     */
    PARTED,
    /**
     * 已成交
     */
    FILLED,
    /**
     * 已取消
     */
    CANCELED
    ;

    public static OpenApiStatus fromOrderStatus(OrderStatus status) {
        switch(status) {
            case PRE_TRIGGER:
                return NEW;
            case PENDING:
                return PENDING;
            case EXECUTING:
                return NEW;
            case LOCKED:
                return NEW;
            case EXCEPTION:
                return NEW;
            case COMPLETED:
                return FILLED;
            case CANCELED:
                return CANCELED;
            case PART_CANCELED:
                return PARTED;
            default:
                throw new RuntimeException("invalid order status: {} for open api");
        }
    }
}

