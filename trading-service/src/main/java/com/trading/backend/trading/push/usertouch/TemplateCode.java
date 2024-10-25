package com.google.backend.trading.push.usertouch;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送消息的模版
 *
 * @author savion.chen
 * @date 2021/11/12 18:28
 */
public enum TemplateCode {
    /**
     * 编号及概要
     */
    SPOT_SUBMIT_TOAST("TOS02_000001", "现货委托提交成功"),
    SPOT_CANCEL_TOAST("TOS02_000002", "现货订单已取消"),
    SPOT_MODIFY_TOAST("TOS02_000027", "现货订单修改成功"),
    MARGIN_SUBMIT_TOAST("TOS02_000006", "杠杆委托提交成功"),
    MARGIN_CANCEL_TOAST("TOS02_000007", "杠杆订单已取消"),
    MARGIN_MODIFY_TOAST("TOS02_000027", "杠杆订单修改成功"),

    SPOT_TRIGGER_EMAIL("EML02_000122", "现货委托触发"),
    SPOT_TRIGGER_TOAST("TOS02_000003", "现货委托触发"),
    SPOT_TRIGGER_PUSH("PUS02_000022", "现货委托触发"),

    SPOT_BUY_EMAIL("EML02_000120", "现货买入成交"),
    SPOT_BUY_TOAST("TOS02_000004", "现货买入成交"),
    SPOT_BUY_PUSH("PUS02_000023", "现货买入成交"),

    SPOT_SELL_EMAIL("EML02_000121", "现货卖出成交"),
    SPOT_SELL_TOAST("TOS02_000005", "现货卖出成交"),
    SPOT_SELL_PUSH("PUS02_000024", "现货卖出成交"),

    MARGIN_TRIGGER_EMAIL("EML02_000119", "杠杆委托触发"),
    MARGIN_TRIGGER_TOAST("TOS02_000008", "杠杆委托触发"),
    MARGIN_TRIGGER_PUSH("PUS02_000026", "杠杆委托触发"),

    MARGIN_LONG_EMAIL("EML02_000118", "杠杆做多成交"),
    MARGIN_LONG_TOAST("TOS02_000009", "杠杆做多成交"),
    MARGIN_LONG_PUSH("PUS02_000027", "杠杆做多成交"),

    MARGIN_SHORT_EMAIL("EML02_000117", "杠杆做空成交"),
    MARGIN_SHORT_TOAST("TOS02_000010", "杠杆做空成交"),
    MARGIN_SHORT_PUSH("PUS02_000025", "杠杆做空成交"),

    STOP_PROFIT_EMAIL("EML02_000116", "仓位止盈触发"),
    STOP_PROFIT_TOAST("TOS02_000011", "仓位止盈触发"),
    STOP_PROFIT_PUSH("PUS02_000029", "仓位止盈触发"),
    STOP_PROFIT_SMS("SMS02_000078", "仓位止盈触发"),
    STOP_PROFIT_IN_MAIL("MSG05_000021", "仓位止盈触发"),

    STOP_LOSS_EMAIL("EML01_000023", "仓位止损触发"),
    STOP_LOSS_TOAST("TOS02_000012", "仓位止损触发"),
    STOP_LOSS_PUSH("PUS02_000051", "仓位止损触发"),
    STOP_LOSS_SMS("SMS02_000079", "仓位止损触发"),
    STOP_LOSS_IN_MAIL("MSG05_000020", "仓位止损触发"),

    STOP_PROFIT_DONE_EMAIL("EML02_000114", "仓位止盈完成"),
    STOP_PROFIT_DONE_TOAST("TOS02_000013", "仓位止盈完成"),
    STOP_PROFIT_DONE_PUSH("PUS02_000052", "仓位止盈完成"),
    STOP_PROFIT_DONE_SMS("SMS01_000025", "仓位止盈完成"),
    STOP_PROFIT_DONE_IN_MAIL("MSG05_000022", "仓位止盈完成"),

    STOP_LOSS_DONE_EMAIL("EML02_000115", "仓位止损完成"),
    STOP_LOSS_DONE_TOAST("TOS02_000014", "仓位止损完成"),
    STOP_LOSS_DONE_PUSH("PUS02_000053", "仓位止损完成"),
    STOP_LOSS_DONE_SMS("SMS01_000080", "仓位止损完成"),
    STOP_LOSS_DONE_IN_MAIL("MSG05_000023", "仓位止损完成"),

    SETTLE_DONE_EMAIL("EML02_000113", "资金费结算完成"),
    SETTLE_DONE_TOAST("TOS02_000015", "资金费结算完成"),
    SETTLE_DONE_PUSH("PUS02_000030", "资金费结算完成"),

    /**
     * 特殊场景，一个场景可能会发送两个邮件，EML02_000112 部分成功，EML02_000138 部分失败
     */
    DELIVERY_OK_TOAST("TOS02_000016", "交割完成"),
    DELIVERY_FAIL_TOAST("TOS02_000017", "交割失败"),
    AUTO_DELIVERY_SUCCESS_EMAIL("EML02_000112", "自动交割成功信息"),
    AUTO_DELIVERY_TOAST("TOS02_000018", "自动交割完成"),
    AUTO_DELIVERY_PUSH("PUS02_000031", "自动交割完成"),

    AUTO_DELIVERY_FAIL_EMAIL("EML02_000138", "自动交割失败信息"),

    NOT_MARGIN_BUY_TOAST("TOS02_000025", "保证金不足买入"),
    NOT_MARGIN_BUY_PUSH("PUS02_000037", "保证金不足买入"),
    NOT_MARGIN_SELL_TOAST("TOS02_000026", "保证金不足卖出"),
    NOT_MARGIN_SELL_PUSH("PUS02_000038", "保证金不足卖出"),

    FORCE_CLOSE_EMAIL("EML02_000111", "风险强平"),
    FORCE_CLOSE_TOAST("TOS02_000024", "风险强平"),
    FORCE_CLOSE_PUSH("PUS02_000054", "风险强平"),
    FORCE_CLOSE_SMS("SMS02_000073", "风险强平"),
    FORCE_CLOSE_IN_MAIL("MSG05_000029", "风险强平"),

    PART_FORCE_CLOSE_EMAIL("EML02_000110", "风险部分强平"),
    PART_FORCE_CLOSE_SMS("SMS02_000072", "风险部分强平"),
    PART_FORCE_CLOSE_IN_MAIL("MSG05_000028", "风险部分强平"),
    PART_FORCE_CLOSE_TOAST("TOS02_000023", "风险部分强平"),
    PART_FORCE_CLOSE_PUSH("PUS02_000036", "风险部分强平"),

    ALARM_PRICE_PUSH("PUS02_000056", "行情预警");


    private final String code;
    private final String name;

    TemplateCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }


    public static List<String> listTypes(List<TemplateCode> codes){
        List<String> res = new ArrayList<>();
        for(TemplateCode item: codes){
            res.add(item.getCode());
        }
        return res;
    }

}
