package com.google.backend.trading.model.trade;

import java.util.ArrayList;
import java.util.List;

/**
 * 成交记录类型
 * @author adam.wang
 * @date 2021/10/8 19:40
 */
public enum TransactionType {
    /**
     * 开仓
     */
    OPEN_POSITION("MARGIN","OPEN_POSITION"),
    /**
     * 加仓
     */
    ADD_POSITION("MARGIN","ADD_POSITION"),
    /**
     * 平仓
     */
    CLOSE_POSITION("MARGIN","CLOSE_POSITION"),
    /**
     * 减仓
     */
    REDUCE_POSITION("MARGIN","REDUCE_POSITION"),
    /**
     * 交割
     */
    SETTLE_POSITION("MARGIN","SETTLE_POSITION"),
    /**
     * 现货
     */
    SPOT("SPOT","SPOT"),
    /**
     * 兑换
     */
    SWAP("SWAP","SWAP");


    private final String code;
    private final String name;

    TransactionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() { return name; }

    public static List<TransactionType> getByCode(String code) {
        List<TransactionType> list = new ArrayList<>();
        for (TransactionType value : TransactionType.values()) {
            if (value.getCode().equals(code)) {
                list.add(value);
            }
        }
        return list;
    }

    public static TransactionType getByName(String name) {
        for (TransactionType value : TransactionType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new RuntimeException("TransactionType not found by name");
    }

    public static boolean isOpenPos(TransactionType type) {
        return (type == TransactionType.OPEN_POSITION || type == TransactionType.ADD_POSITION);
    }

    public static boolean isClosePos(TransactionType type) {
        return (type == TransactionType.CLOSE_POSITION || type == TransactionType.REDUCE_POSITION);
    }
}
