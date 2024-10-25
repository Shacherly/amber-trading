package com.google.backend.trading.exception;

import com.google.backend.trading.model.common.Response;

/**
 * 业务异常码（用于多语言转换，暴露给用户侧）
 * 交易模块错误码前缀 24
 * 错误码长度8位：{2位模块错误码前缀，10-20预留，使用20-99}{6位业务自定义唯一，100xxx作为不同模块通用错误码}
 *
 * @author trading
 * @date 2021/8/4 17:25
 */
public enum BusinessExceptionEnum {
    /// 对外统一简化状态码
    SUCCESS(0, "success"),
    FAILED(-1, "failed"),

    // 对内服务状态码
    DUPLICATE_IDEMPOTENT_REQUEST(0, "duplicate-idempotent-request"),

    /**
     * 内部代码
     */
    ASSET_FALLBACK(1001, "asset fallback"),
    PDT_FALLBACK(1002, "pdt fallback"),
    COMMON_CONFIG_FALLBACK(1003, "common config fallback"),
    ALARM_FALLBACK(1004, "alarm fallback"),
    CLUB_FALLBACK(1005, "club fallback"),

    TIME_OUT_FAIL(24100014, "请求处理超时"),
    REQUEST_TOO_MANY(24100015, "请求过多"),

    COMPLIANCE_LIMIT(24100004, "因当地法律法规限制，该等服务不向阁下所在地区提供。"),

    /**
     * 现货/杠杆/swap订单
     */
    ORDER_CHANGE_OR_NOT_FOUND(24200101, "订单状态发生更改，请重试"),
    REFUSE_OPERATE_IN_RISK_LIQUID(24200102, "账户处于风控清算中，暂不支持此操作，请稍后"),
    OVER_MARGIN_POSITION_LIMIT(24200103, "该笔订单超过持仓限额，请重新确认订单"),
    ORDER_AMOUNT_TOO_SMALL(24200104, "订单金额过小"),
    ORDER_AMOUNT_TOO_LARGE(24200105, "订单金额过大"),
    TRADE_MARKET_UNDER_MAINTENANCE(24200106, "该交易市场正维护中，暂不支持新增委托，请稍后"),
    OVER_ORDER_MAXIMUM_NUM(24200107, "当前活跃委托已经达上限"),
    MARGIN_NEED_KEY(24200108, "杠杆交易前请先完成身份认证"),
    MARGIN_NOT_SUPPORT_USA_CLIENT(24200109, "杠杆交易暂不支持美国用户"),
    FOK_IOC_LIMIT_PRICE_NOT_REACH(24200110, "FOK/IOC限价不满足价格"),
    CANCEL_ORDER_FAIL(24200111, "订单取消失败"),

    SYMBOL_NOT_SUPPORT_SPOT(24220111, "symbol暂不支持现货"),
    SYMBOL_NOT_SUPPORT_MARGIN(24220112, "symbol暂不支持杠杆"),
    SYMBOL_NOT_SUPPORT_SWAP(24220113, "symbol暂不支持兑换"),

    /**
     * 仓位
     */
    POSITION_CHANGE_OR_NOT_FOUND(24200301, "仓位状态发生了变更，请重试"),

    /**
     * 资金
     */
    INSUFFICIENT_FUNDS(24200401, "余额不足"),

    /**
     * 设置
     */
    EXIST_ORDER_NOT_SUPPORT_CHANGE_LEVERAGE(24200501, "存在当前活跃杠杆委托，不支持修改交易杠杆倍数"),
    OVER_POSITION_LIMIT_NOT_SUPPORT_CHANGE_LEVERAGE(24200502, "当前持仓限额不支持修改交易杠杆倍数"),
    MARGIN_INSUFFICIENT_NOT_SUPPORT_CHANGE_LEVERAGE(24200503, "总保证金不足不支持修改交易杠杆倍数"),
    EXIST_ORDER_NOT_SUPPORT_CLOSE_EARN_PLEDGE(24200504, "存在当前活跃杠杆委托暂不支持关闭理财"),
    TOO_HIGH_RISK_NOT_SUPPORT_CLOSE_EARN_PLEDGE(24200505, "当前持仓风险等级暂不支持关闭理财质押"),
    TASK_PROFIT_NOT_SUPPORT(24200506, "全局止盈值无效"),
    STOP_LOSS_NOT_SUPPORT(24200507, "全局止损值无效"),
    CHANGE_TIMEZONE_NOT_SUPPORT(24200508, "24小时内不允许修改结算时区"),
    ALARM_PRICE_SET_MAXIMUM_NUM(24200509, "设置预警价格超过最大次数"),

    /**
     * OPEN-API
     */
    OPEN_API_SYMBOL_NOT_EXIST(24200901, "symbol not exist"),
    OPEN_API_POSITION_NOT_EXIST(24200902, "position not exist"),
    OPEN_API_ORDER_NOT_EXIST(24200903, "order not exist"),
    OPEN_API_OVER_MAX_POSITION_QUANTITY(24200904, "over max position quantity"),

    /**
     * 查询
     */
    SEARCH_OVER_RANGE(24220601, "查询跨度过大"),

    TRADE_NEED_KEY(24200700, "交易需要用户kyc"),


    /**
     * 兜底错误 24200000
     */
    UNEXPECTED_ERROR(Response.COMMON_FAIL, "数据加载失败，请稍后重试"),


    /// 测试保留使用
    TEST_DEMO(24000000, "demo exception"),

    // 5xx 杠杆问题
    INSUFFICIENT_MARGIN(24100500, "insufficient margin"),
    EXCEED_TOTAL_POSITION_LIMIT(24100501, "exceed total position limit"),
    EXCEED_SYMBOL_POSITION_LIMIT(24100502, "exceed symbol position limit"),
    REDUCE_ONLY(24100503, "reduce only"),
   EXPORT_EXCEPTION(24600001, "导出异常"),
    ;


    private final int code;
    private final String msg;

    BusinessExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
