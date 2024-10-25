package com.google.backend.trading.model.sensors;

public enum SensorsSwapOrderStatus {
    /**
     * 完全成交
     */
    COMPLETED(0),
    /**
     * 完全取消
     */
    CANCELED(1),
    /**
     * 部分成交取消
     */
    PART_CANCELED(1),
    ;


    private final int code;

    SensorsSwapOrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Number getCodeByName(String name) {
        for (SensorsSwapOrderStatus value : SensorsSwapOrderStatus.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
