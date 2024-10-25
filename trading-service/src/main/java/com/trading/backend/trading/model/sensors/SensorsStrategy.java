package com.google.backend.trading.model.sensors;

public enum SensorsStrategy {
    GTC(1),
    IOC(2),
    FOK(3),
    ;

    private final int code;

    SensorsStrategy(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Number getCodeByName(String name) {
        for (SensorsStrategy value : SensorsStrategy.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
