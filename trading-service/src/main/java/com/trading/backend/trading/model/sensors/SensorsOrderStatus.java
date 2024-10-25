package com.google.backend.trading.model.sensors;

public enum SensorsOrderStatus {
    /**
     * 待触发
     */
    PRE_TRIGGER (0),
    /**
     * 等待处理，调用资金判定的中间状态
     */
    PENDING (0),
    /**
     * 挂单中
     */
    EXECUTING (1),
    /**
     * 订单发单中
     */
    LOCKED(1),
    /**
     * 执行异常，（保证金不足，持仓上限等，具体原因会记录到error字段） margin单独有
     */
    EXCEPTION(5),

    /**
     * 完全成交
     */
    COMPLETED(4),
    /**
     * 完全取消
     */
    CANCELED(3),
    /**
     * 部分成交取消
     */
    PART_CANCELED(3),
    ;


    private final int code;

    SensorsOrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Number getCodeByName(String name) {
        for (SensorsOrderStatus value : SensorsOrderStatus.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
