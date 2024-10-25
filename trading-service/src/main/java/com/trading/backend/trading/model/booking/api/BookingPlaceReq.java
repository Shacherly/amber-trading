package com.google.backend.trading.model.booking.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * OTC_shop下单请求
 *
 * @author savion.chen
 * @date 2021/10/25 18:23
 */
@Data
@ApiModel(value = "booking下单")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class BookingPlaceReq {

    @ApiModelProperty(value = "用户id", required = true, example = "345612")
    private String uid;

    @ApiModelProperty(value = "唯一标识，用于幂等", example = "1111")
    private String uniqueId;

    @ApiModelProperty(value = "订单类型:LIMIT,MARKET", example = "LIMIT")
    private String type;

    @ApiModelProperty(value = "订单策略:FOK,IOC,GTC", example = "FOK")
    private String strategy;

    @NotBlank
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotBlank
    @ApiModelProperty(value = "方向", required = true, example = "BUY")
    private String direction;

    @NotNull
    @Positive
    @Max(value = Long.MAX_VALUE)
    @ApiModelProperty(value = "成交数量", required = true, example = "100")
    private BigDecimal filledQuantity;

    @NotNull
    @Positive
    @Max(value = Long.MAX_VALUE)
    @ApiModelProperty(value = "成交价格", required = true, example = "31415")
    private BigDecimal filledPrice;

    @ApiModelProperty(value = "备注", example = "梭哈抄底")
    private String notes;
}
