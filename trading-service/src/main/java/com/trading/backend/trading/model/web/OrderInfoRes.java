package com.google.backend.trading.model.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.backend.trading.config.web.I18nConvertSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "web杠杆订单信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderInfoRes {

    @ApiModelProperty(value = "订单id", example = "12312")
    private String orderId;

    @ApiModelProperty(value = "是否现货", example = "true")
    private Boolean isSpot;

    @ApiModelProperty(value = "方向:SELL、BUY", notes = "1 买，2卖", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "订单类型,LIMIT MARKET STOP-LIMIT STOP-MARKET",
            required = true, notes="1: 限价单 2: 市价单 3: 条件限价单 4: 条件市价单", example = "LIMIT")
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

    @ApiModelProperty(value = "订单状态 PRE_TRIGGER 待触发, EXECUTING 挂单中, EXCEPTION 异常, CANCELED 已取消, PART_CANCELED 部分成交取消, COMPLETED 完全成交",
            notes = "1 待触发，2 执行中，3 已完成，4 取消", example = "CANCELED")
    private String status;

    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;

    @ApiModelProperty(value = "备注", example = "0.001")
    private String notes;

    @ApiModelProperty(value = "错误", example = "0.001")
    @JsonSerialize(using = I18nConvertSerializer.class)
    private String error;

    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;

    @ApiModelProperty(value = "更新时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date mtime;

    @ApiModelProperty(value = "只减仓",  example = "false")
    private Boolean reduceOnly;

    @ApiModelProperty(value = "订单来源：PLACED_BY_CLIENT 客户 OTC_SHOP 场外 FORCE_CLOSE 强平", example = "PLACED_BY_CLIENT")
    private String source;

    @ApiModelProperty(value = "是否quote发单", example = "false")
    private Boolean isQuote;
}
