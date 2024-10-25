package com.google.backend.trading.sensors;

/**
 * event命名规范 模块名为前缀，单词间用 . 分割
 *
 * @author trading
 * @date 2021/10/23 15:40
 */
public enum SensorsEventEnum {
    SWAP_SUBMIT("SwapSubmit"),

    SPOT_SUBMIT("SpotSubmit"),

    SPOT_STATUS_CHANGE("SpotStatusChange"),

    MARGIN_SUBMIT("MarginSubmit"),

    MARGIN_OPEN("MarginOpen"),

    MARGIN_CLOSE("MarginClose"),

    SETTLE_SUCCESS("SettleSuccess"),

    TRADE_SETTING_CHANGE("TradeSettingChange"),

    SET_ALERT("SetAlert"),

    MARKET_STATUS_CHANGE("MarketStatusChange"),

    ;
    private String code;

    SensorsEventEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
