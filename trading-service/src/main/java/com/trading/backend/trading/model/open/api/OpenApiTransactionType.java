package com.google.backend.trading.model.open.api;

import com.google.backend.trading.model.trade.TransactionType;

public enum OpenApiTransactionType {
    /**
     * 现货
     */
    SPOT,
    /**
     * 开仓
     */
    OPEN,
    /**
     * 平仓
     */
    CLOSE,
    /**
     * 交割
     */
    SETTLE;

    public static OpenApiTransactionType fromTransactionType(TransactionType type) {
        switch (type) {
            case OPEN_POSITION:
                return OPEN;
            case ADD_POSITION:
                return OPEN;
            case CLOSE_POSITION:
                return CLOSE;
            case REDUCE_POSITION:
                return CLOSE;
            case SPOT:
                return SPOT;
            case SETTLE_POSITION:
                return SETTLE;
            default:
                throw new Error("invalid transaction type: {} for open api");
        }
    }

}
