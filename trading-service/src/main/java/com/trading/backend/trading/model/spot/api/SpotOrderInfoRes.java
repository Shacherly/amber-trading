package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @create 2021/9/27
 */
@ApiModel(value = "现货委托信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotOrderInfoRes {

    @ApiModelProperty(value = "订单id", example = "123123")
    private String orderId;

    @ApiModelProperty(value = "方向,BUY 买，SELL卖", required = true,
            notes="BUY 买，SELL卖", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "币对", required = true, example = "ETH_USD")
    private String symbol;

    @ApiModelProperty(value = "订单类型,LIMIT MARKET STOP-LIMIT STOP-MARKET",
            required = true, notes="1: 限价单 2: 市价单 3: 条件限价单 4: 条件市价单", example = "LIMIT")
    private String type;

    @ApiModelProperty(value = "执行策略,FOK IOC GTC", required = true,
            notes="1:FOK 2:IOC 3:GTC", example = "FOK")
    private String strategy;

    @ApiModelProperty(value = "委托数量", required = true,
            notes="base数量成交即base数量，quote数量成交即quote数量", example = "1231")
    private BigDecimal quantity;

    @ApiModelProperty(value = "成交数量，未发生成交时，数量为 0", example = "123213")
    private BigDecimal filledQuantity = BigDecimal.ZERO;

    @ApiModelProperty(value = "委托价格", notes="limit单传入", example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "触发价格", example = "4563")
    private BigDecimal triggerPrice;

    @ApiModelProperty(value = "大于/小于触发价", allowableValues = ">,<", example = ">")
    private String triggerCompare;

    @ApiModelProperty(value = "成交价格", example = "4321")
    private BigDecimal filledPrice;

    @ApiModelProperty(value = "订单状态 PRE_TRIGGER 待触发, EXECUTING 挂单中, EXCEPTION 异常, CANCELED 已取消, PART_CANCELED 部分成交取消, COMPLETED 完全成交",
            notes = "1 待触发，2 执行中，3 已完成，4 取消", example = "CANCELED")
    private String status;

    @ApiModelProperty(value = "手续费", example = "3.123")
    private BigDecimal fee;

    @ApiModelProperty(value = "费率币种", example = "USDT")
    private String feeCoin;

    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Date ctime;

    @ApiModelProperty(value = "更新时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Date mtime;

    @ApiModelProperty(value = "是否quote发单", example = "true")
    private Boolean isQuote;

}
