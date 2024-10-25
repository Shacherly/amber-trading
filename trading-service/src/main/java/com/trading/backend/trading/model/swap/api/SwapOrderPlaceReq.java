package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:42
 */
@ApiModel(value = "兑换接口请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapOrderPlaceReq {

    @NotNull(message = "支付币种 must not be null")
    @ApiModelProperty(name="from_coin",value = "支付币种", required = true, example = "BTC")
    private String fromCoin;

    @NotNull(message = "获得币种 must not be null")
    @ApiModelProperty(name="to_coin",value = "获得币种", required = true, example = "USD")
    private String toCoin;

    @NotNull(message = "委托模式 must not be null")
    @ApiModelProperty(value = "委托模式: PAYMENT, OBTAINED", notes = "1:委托支付 2:委托获得",
            required = true, example = "PAYMENT")
    @Pattern(regexp="PAYMENT|OBTAINED",message = "委托模式不正确")
    private String mode;

    @NotNull(message = "price must not be null")
    @ApiModelProperty(value = "委托价格", required = true, example = "1231.2")
    @Max(value = Long.MAX_VALUE)
    @Positive
    private BigDecimal price;

    @NotNull(message = "quantity must not be null")
    @ApiModelProperty(value = "委托数量", required = true, example = "123")
    @Max(value = Long.MAX_VALUE)
    @Positive
    private BigDecimal quantity;

    @ApiModelProperty(hidden = true)
    private String source;
}
