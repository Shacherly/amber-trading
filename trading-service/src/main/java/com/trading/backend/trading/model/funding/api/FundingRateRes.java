package com.google.backend.trading.model.funding.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 17:29
 */
@ApiModel(value = "实时费率返回实体")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FundingRateRes {
    @ApiModelProperty(value = "币种", example = "BTC")
    private String coin;
    @ApiModelProperty(value = "借出利率", example = "0.001")
    private BigDecimal borrow;
    @ApiModelProperty(value = "返回利率", example = "0.001")
    private BigDecimal lend;
}
