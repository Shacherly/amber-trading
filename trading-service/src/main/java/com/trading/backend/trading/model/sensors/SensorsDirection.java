package com.google.backend.trading.model.sensors;

public enum SensorsDirection {
    BUY("买"),
    SELL("卖"),
    ;

    private final String code;

    SensorsDirection(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getCodeByName(String name) {
        for (SensorsDirection value : SensorsDirection.values()) {
            if (value.name().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
