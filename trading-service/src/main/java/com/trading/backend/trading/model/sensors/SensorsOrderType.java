package com.google.backend.trading.model.sensors;

public enum SensorsOrderType {
    MARKET(1),
    LIMIT(2),
    STOP_LIMIT(3),
    STOP_MARKET(4),
    ;

    private final int code;

    SensorsOrderType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Number getCodeByName(String name) {
        for (SensorsOrderType value : SensorsOrderType.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
