package com.google.backend.trading.model.internal.crypt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/10/08
 */
@Data
@ApiModel(value = "买币服务现货下单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CryptSpotOrderPlaceReq {

    @NotBlank(message = "orderId不能为空")
    @ApiModelProperty(name = "order_id", required = true, example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true, example = "33345")
    private String uid;

    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "支付币对", allowableValues = "USD",required = true, example = "USD")
    private String fromCoin;

    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "获得币对", allowableValues = "ETH, BTC", required = true, example = "BTC")
    private String toCoin;

    @NotNull(message = "支付数量不能为空")
    @ApiModelProperty(value = "支付数量", required = true,
            notes = "支付数量", example = "1231")
    @Positive
    @Max(value = Long.MAX_VALUE)
    private BigDecimal quantity;
}
