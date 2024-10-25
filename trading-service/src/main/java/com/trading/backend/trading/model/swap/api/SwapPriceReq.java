package com.google.backend.trading.model.swap.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:42
 */
@ApiModel(value = "兑换报价请求")
@Data
@RequestUnderlineToCamel
public class SwapPriceReq {

    @NotNull(message = "from_coin must not be null")
    @ApiModelProperty(name="from_coin",value = "支付币种", required = true, example = "BTC")
    private String fromCoin;

    @NotNull(message = "to_coin must not be null")
    @ApiModelProperty(name="to_coin",value = "获得币种", required = true, example = "USD")
    private String toCoin;

    @ApiModelProperty(value = "委托模式：PAYMENT, OBTAINED", notes = "1 委托支付 2 委托获得", example = "OBTAINED")
    @Pattern(regexp = "PAYMENT|OBTAINED", message = "委托模式不正确")
    private String mode;

    @NotNull(message = "quantity must not be null")
    @Max(value = Long.MAX_VALUE)
    @ApiModelProperty(value = "委托数量", example = "10")
    private BigDecimal quantity;

    @ApiModelProperty(value = "是否免手续费", hidden = true)
    private Boolean feeFree;
}
