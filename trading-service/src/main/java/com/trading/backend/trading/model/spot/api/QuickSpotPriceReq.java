package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2021/12/28 15:01
 */
@ApiModel(value = "WEB 快捷兑换报价请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuickSpotPriceReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "交易方向", required = true, example = "BUY")
    private String direction;

    @ApiModelProperty(value = "委托数量", example = "123")
    @Max(value = Long.MAX_VALUE)
    private BigDecimal quantity = BigDecimal.ONE;
}
