package com.google.backend.trading.model.swap.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/2/7 15:38
 */

@ApiModel(value = "定投兑换接口kafka消息体")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AipSwapOrderPlaceReqDTO {

    @NotNull(message = "定投计划id")
    @ApiModelProperty(name = "aip_swap_id", value = "订单id", required = true, example = "")
    private String aipSwapId;

    @NotNull(message = "支付币种 must not be null")
    @ApiModelProperty(name = "from_coin", value = "支付币种", required = true, example = "USD")
    private String fromCoin;

    @NotNull(message = "获得币种 must not be null")
    @ApiModelProperty(name = "to_coin", value = "获得币种", required = true, example = "BTC")
    private String toCoin;

    @NotNull(message = "quantity must not be null")
    @ApiModelProperty(value = "委托数量", required = true, example = "400")
    @Positive
    private BigDecimal quantity;

    @NotNull(message = "委托模式 must not be null")
    @ApiModelProperty(value = "委托模式: PAYMENT, OBTAINED", notes = "1:委托支付 2:委托获得",
            required = true, example = "PAYMENT")
    @Pattern(regexp = "PAYMENT|OBTAINED", message = "委托模式不正确")
    private String mode;

    @NotNull(message = "uid must not be null")
    @ApiModelProperty(name = "uid", value = "用户id", required = true, example = "123")
    private String uid;

}
