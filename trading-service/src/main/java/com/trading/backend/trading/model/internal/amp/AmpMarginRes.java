package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "APM杠杆返回对象")
public class AmpMarginRes {

    @ApiModelProperty(value = "订单id", example = "12312")
    private String orderId;
    @ApiModelProperty(value = "uid", example = "12312")
    private String uid;
    @ApiModelProperty(value = "方向", notes = "BUY 买，SELL卖", example = "BUY")
    private String direction;
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "订单类型", notes = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", example = "LIMIT")
    private String type;
    @ApiModelProperty(value = "执行策略 可能为null", notes = "FOK,IOC,GTC", example = "FOK")
    private String strategy;
    @ApiModelProperty(value = "委托数量", example = "100")
    private BigDecimal quantity;
    @ApiModelProperty(value = "成交数量", example = "50")
    private BigDecimal filledQuantity;
    @ApiModelProperty(value = "委托价格 可能为null", notes = "LIMIT单", example = "50000")
    private BigDecimal price;
    @ApiModelProperty(value = "触发价格 可能为null", notes = "STOP单", example = "40000")
    private BigDecimal triggerPrice;
    @ApiModelProperty(value = "大于/小于触发价格 可能为null", example = ">")
    private String triggerCompare;
    @ApiModelProperty(value = "成交价格", example = "50000")
    private BigDecimal filledPrice;
    @ApiModelProperty(value = "仓位状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) " +
			"PART_CANCELED(部分成交取消)", example = "COMPLETED")
    private String status;
    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;
    @ApiModelProperty(value = "手续费币种", example = "USD")
    private String feeCoin = "USD";
    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
    @ApiModelProperty(value = "更新时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date mtime;
    @ApiModelProperty(value = "提示信息", example = "1632835745000")
    private String notes;
    @ApiModelProperty(value = "只减仓", example = "false")
    private Boolean reduceOnly;
    @ApiModelProperty(value = "PLACED_BY_CLIENT（用户下的手工单） PLACED_BY_API（用户通过API下的程序化订单） LIQUIDATION（清算订单） AUTO_CONVERSION（盈亏转换） OTC_SHOP(BOOKING用户来源)\n" +
            "  FORCE_CLOSE（强平或强制减仓）LOAN_LIQUIDATION（借贷强平订单）EARN_LIQUIDATION（理财清算）REPAY_WITH_COLLATERAL（借贷质押还款订单）AUTO_POSITION_SETTLE（自动交割）\n" +
            "  TAKE_PROFIT_STOP_LOSS（止盈止损）", example = "PLACED_BY_CLIENT")
    private String source;
    @ApiModelProperty(value = "错误信息 可能为null", example = "TAKE_PROFIT_STOP_LOSS")
    private String error;

}
