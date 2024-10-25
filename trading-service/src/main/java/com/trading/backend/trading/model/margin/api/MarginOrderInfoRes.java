package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "杠杆订单信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderInfoRes {

    @ApiModelProperty(value = "订单id", example = "12312")
    private String orderId;
    @ApiModelProperty(value = "方向", notes = "1 买，2卖", example = "1")
    private String direction;
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "订单类型", notes = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", example = "LIMIT")
    private String type;
    @ApiModelProperty(value = "执行策略", notes = "FOK,IOC,GTC", example = "FOK")
    private String strategy;
    @ApiModelProperty(value = "委托数量", example = "100")
    private BigDecimal quantity;
    @ApiModelProperty(value = "成交数量", example = "50")
    private BigDecimal quantityFilled;
    @ApiModelProperty(value = "委托价格", notes = "LIMIT单", example = "50000")
    private BigDecimal price;
    @ApiModelProperty(value = "触发价格", notes = "STOP单", example = "<40000")
    private BigDecimal triggerPrice;
    @ApiModelProperty(value = "大于/小于触发价格", example = ">")
    private String triggerCompare;
    @ApiModelProperty(value = "成交价格", example = "50000")
    private BigDecimal filledPrice;
    @ApiModelProperty(value = "订单状态", notes = "订单状态 pre_trigger  pending  executing  locked completed canceled error_canceled system_cancelling system_canceled", example = "true")
    private String status;
    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;
    @ApiModelProperty(value = "手续费币种", example = "USD")
    private String feeCoin;
    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
    @ApiModelProperty(value = "更新时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date mtime;
    @ApiModelProperty(value = "提示信息", example = "1632835745000")
    private String notes;
    @ApiModelProperty(value = "只减仓", example = "false")
    private Boolean reduceOnly;
    @ApiModelProperty(value = "来源，TAKE_PROFIT_STOP_LOSS表示止盈止损订单", example = "TAKE_PROFIT_STOP_LOSS")
    private String source;
    @ApiModelProperty(value = "来源，TAKE_PROFIT_STOP_LOSS表示止盈止损订单", example = "TAKE_PROFIT_STOP_LOSS")
    private String error;
}
