package com.google.backend.trading.model.sensors;

public enum SensorsSourceType {
    // 用户下的手工单
    PLACED_BY_CLIENT("用户下手工单"),
    // 用户通过API下的程序化订单
    PLACED_BY_API("用户API下程序单"),
    // 清算订单
    LIQUIDATION("清算订单"),
    //盈亏转换
    AUTO_CONVERSION("盈亏转换"),
    //BOOKING用户来源
    OTC_SHOP("BOOKING用户来源"),
    // 强平或强制减仓
    FORCE_CLOSE("强平或强制减仓"),
    // 借贷强平订单
    LOAN_LIQUIDATION("借贷强平订单"),
    // 理财清算
    EARN_LIQUIDATION("理财清算"),
    // 借贷质押还款订单
    REPAY_WITH_COLLATERAL("借贷质押还款订单"),
    // 自动交割
    AUTO_POSITION_SETTLE("自动交割"),
    // 止盈止损
    TAKE_PROFIT_STOP_LOSS("止盈止损"),
    ;

    private final String code;

    SensorsSourceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getCodeByName(String name) {
        for (SensorsSourceType value : SensorsSourceType.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
