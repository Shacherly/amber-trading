package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author savion.chen
 * @date 2021/10/09 10:53
 */
@ApiModel(value = "查询币种可用余额")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoinBalanceRes {

    @ApiModelProperty(value = "币种名称", example = "usd")
    private String coin;

    @ApiModelProperty(value = "可用资金", example = "0.5")
    private BigDecimal balance;

    @ApiModelProperty(value = "可用资金折算成usd", example = "0.5")
    private BigDecimal marketValue;

    @ApiModelProperty(value = "收益 Profit", example = "0.5")
    private BigDecimal profit = BigDecimal.ZERO;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private int priority;
}
