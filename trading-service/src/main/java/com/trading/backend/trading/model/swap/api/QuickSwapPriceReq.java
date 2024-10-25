package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:42
 */
@ApiModel(value = "快捷兑换报价请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuickSwapPriceReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "交易方向", required = true, example = "BUY")
    private String direction;

    @Max(value = Long.MAX_VALUE)
    @ApiModelProperty(value = "委托数量", example = "123")
    private BigDecimal quantity = BigDecimal.ONE;
}
