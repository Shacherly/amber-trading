package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货下单请求")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotOrderPlaceReq {

    @NotBlank(message = "symbol must not be null")
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotBlank(message = "type must not be null")
    @ApiModelProperty(value = "订单类型，LIMIT、MARKET、STOP-LIMIT、STOP-MARKET", required = true,
            notes="1: 限价单 2:市价单 3:限价条件单 4:市价条件单", example = "LIMIT")
    private String type;

    @Pattern(regexp = "GTC|IOC|FOK")
    @ApiModelProperty(value = "执行策略, GTC, IOC, FOK", required = true,
            notes="1:GTC 2:IOC 3:FOK", example = "GTC")
    private String strategy = "GTC";

    @NotBlank(message = "direction must not be null")
    @ApiModelProperty(value = "方向，BUY SELL", required = true,
            notes="BUY:买 SELL:卖", example = "BUY")
    private String direction;

    @NotNull(message = "is_quote must not be null")
    @ApiModelProperty(name="is_quote", value = "是否按照quote数量成交", required = true, example = "true")
    private Boolean isQuote;

    @NotNull(message = "quantity must not be null")
    @ApiModelProperty(value = "委托数量", required = true,
            notes="isQuote为真按quote数量，否则base数量",dataType = "String", example = "1231")
    @Positive
    @Digits(integer = 32, fraction = 16)
    private BigDecimal quantity;

    @ApiModelProperty(value = "委托价格", notes="limit单传入",dataType = "String",  example = "1231.2")
    @Positive
    @Max(value = Long.MAX_VALUE)
    private BigDecimal price;

    @ApiModelProperty(name="trigger_compare",value = "触发条件 >、<",
            notes="条件单传入大于或小于", example = ">")
    @Pattern(regexp = ">|<")
    private String triggerCompare;

    @ApiModelProperty(name="trigger_price",value = "触发价格", dataType = "String", notes="条件单传入", example = "21312")
    @Positive
    @Max(value = Long.MAX_VALUE)
    private BigDecimal triggerPrice;

    @ApiModelProperty(value = "备注", example = "备注")
    private String notes;
}
