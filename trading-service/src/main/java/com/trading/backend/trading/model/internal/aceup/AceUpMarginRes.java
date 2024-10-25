package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2022/3/17 19:22
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠订单list返回")
public class AceUpMarginRes {
    @ApiModelProperty(name = "uid", value = "用户编码", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String uid;

    @ApiModelProperty(name = "order_id", value = "仓位ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String orderId;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "只减仓", example = "true")
    private Boolean reduceOnly;

    @ApiModelProperty(value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
            example = "MARKET")
    private String type;

    @ApiModelProperty(value = "订单策略:FOK,IOC,GTC ,可能为null", allowableValues = "FOK,IOC,GTC", example = "FOK")
    private String strategy = "GTC";

    @ApiModelProperty(value = "委托数量",
            notes = "base数量成交即base数量，quote数量成交即quote数量", example = "1231")
    private BigDecimal quantity;

    @ApiModelProperty(value = "委托价格 可能为null", notes = "limit单传入", example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "委托成交数量", example = "4563")
    private BigDecimal quantityFilled;

    @ApiModelProperty(value = "手续费", example = "3.123")
    private BigDecimal fee;

    @ApiModelProperty(value = "成交均价", example = "4321")
    private BigDecimal filledPrice;

    @ApiModelProperty(value = "大于/小于触发价 可能为null", allowableValues = ">,<", example = ">")
    private String triggerCompare;

    @ApiModelProperty(value = "for STOP_LIMIT, STOP_MARKET 触发价", example = "4321")
    private String triggerPrice;

    @ApiModelProperty(value = "仓位状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) " +
            "PART_CANCELED(部分成交取消)", example = "COMPLETED")
    private String status;

    @ApiModelProperty(value = "PLACED_BY_CLIENT（用户下的手工单） PLACED_BY_API（用户通过API下的程序化订单） LIQUIDATION（清算订单） AUTO_CONVERSION（盈亏转换） OTC_SHOP(BOOKING用户来源)\n" +
            "  FORCE_CLOSE（强平或强制减仓）LOAN_LIQUIDATION（借贷强平订单）EARN_LIQUIDATION（理财清算）REPAY_WITH_COLLATERAL（借贷质押还款订单）AUTO_POSITION_SETTLE（自动交割）\n" +
            "  TAKE_PROFIT_STOP_LOSS（止盈止损）", example = "PLACED_BY_CLIENT")
    private String source;

    @ApiModelProperty(value = "总结者")
    private String terminator;

    @ApiModelProperty(value = "错误信息 可能为null")
    private String error;

    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty(value = "创建时间")
    private Date ctime;

    @ApiModelProperty(value = "修改时间")
    private Date mtime;
}
