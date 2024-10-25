package com.google.backend.trading.model.trade;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 订单来源类型
 *
 * @author savion.chen
 * @date 2021/9/29 18:28
 */
@Slf4j
public enum SourceType {
    // 用户下的手工单
    PLACED_BY_CLIENT("PLACED_BY_CLIENT", "PLACED_BY_CLIENT"),
    // 用户通过API下的程序化订单
    PLACED_BY_API("PLACED_BY_API", "PLACED_BY_API"),
    // 清算订单
    LIQUIDATION("LIQUIDATION", "LIQUIDATION"),
    //盈亏转换
    AUTO_CONVERSION("AUTO_CONVERSION", "AUTO_CONVERSION"),
    //BOOKING用户来源
    OTC_SHOP("OTC_SHOP", "OTC_SHOP"),
    // 强平或强制减仓
    FORCE_CLOSE("FORCE_CLOSE", "FORCE_CLOSE"),
    // 借贷强平订单
    LOAN_LIQUIDATION("LOAN_LIQUIDATION", "LOAN_LIQUIDATION"),
    // 理财清算
    EARN_LIQUIDATION("EARN_LIQUIDATION", "EARN_LIQUIDATION"),
    // 借贷质押还款订单
    REPAY_WITH_COLLATERAL("REPAY_WITH_COLLATERAL", "REPAY_WITH_COLLATERAL"),
    // 自动交割
    AUTO_POSITION_SETTLE("AUTO_POSITION_SETTLE", "AUTO_POSITION_SETTLE"),
    // 止盈止损
    TAKE_PROFIT_STOP_LOSS("TAKE_PROFIT_STOP_LOSS", "TAKE_PROFIT_STOP_LOSS"),
    //买币服务币种转换
    BUY_CRYPTOCURRENCY_CONVERSION("BUY_CRYPTOCURRENCY_CONVERSION", "BUY_CRYPTOCURRENCY_CONVERSION"),
    //定投
    AUTOMATIC_INVESTMENT_PLAN("AUTOMATIC_INVESTMENT_PLAN", "AUTOMATIC_INVESTMENT_PLAN"),
    ;

    private final String code;
    private final String name;

    SourceType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() { return name; }

    public static SourceType getByCode(String code) {
        for (SourceType value : SourceType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new RuntimeException("SourceType not found by code");
    }

    public static SourceType getByName(String name) {
        for (SourceType value : SourceType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isFromUser(String name) {
        SourceType source = SourceType.getByName(name);
        return source == SourceType.PLACED_BY_CLIENT || source == SourceType.PLACED_BY_API;
    }

    public static boolean isLoan(SourceType type) {
        return (type == SourceType.REPAY_WITH_COLLATERAL || type == SourceType.LOAN_LIQUIDATION);
    }

    public boolean isFromUser() {
        return this == SourceType.PLACED_BY_CLIENT || this == SourceType.PLACED_BY_API;
    }

    /**
     * 非系统单
     */
    public static final List<String> CLIENT_ORDER = Arrays.asList(SourceType.PLACED_BY_API.getName(),SourceType.PLACED_BY_CLIENT.getName());
}
