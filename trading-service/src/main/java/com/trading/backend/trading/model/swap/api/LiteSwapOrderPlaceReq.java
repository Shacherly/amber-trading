package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/1/5 15:03
 */
@ApiModel(value = "lite版本兑换接口请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LiteSwapOrderPlaceReq {
    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "交易方向", required = true, example = "BUY")
    private String direction;

    @NotNull
    @Positive
    @ApiModelProperty(value = "委托价格", required = true, example = "1231.2")
    @Max(value = Long.MAX_VALUE)
    private BigDecimal price;

    @NotNull
    @Positive
    @ApiModelProperty(value = "委托数量 不管BUG还是SELL quantity都是 base", required = true, example = "123")
    @Max(value = Long.MAX_VALUE)
    private BigDecimal quantity;

}
