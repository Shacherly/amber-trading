package com.google.backend.trading.push.usertouch;


/**
 * 消息的场景类型
 *
 * @author savion.chen
 * @date 2021/11/12 18:11
 */
public enum ScenarioType {

    /**
     * 编号及概要
     */
    ENTRUST_SUBMIT("1", "委托提交"),
    ENTRUST_CANCEL("2", "委托取消"),
    ENTRUST_MODIFY("3", "委托取消"),

    ENTRUST_TRIGGER("4", "委托触发"),
    ENTRUST_TRADED("5", "委托成交"),

    STOP_SINGLE_POS("7", "单个仓位的止盈止损"),
    STOP_CROSSED_POS("8", "全仓的止盈止损"),

    SETTLE("10", "资金费用结算"),
    DELIVERY("11", "交割"),
    NO_MARGIN("12", "保证金不足"),

    FORCE_CLOSE("13", "仓位强平"),
    PART_FORCE_CLOSE("14", "部分仓位强平"),
    ;

    private final String code;
    private final String name;

    ScenarioType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }


}
