package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货可用余额返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotAvailableRes {

    @ApiModelProperty(value = "base币种的现货可用余额", example = "123213")
    private BigDecimal baseAvailable;

    @ApiModelProperty(value = "quote币种的现货可用余额", example = "12312")
    private BigDecimal quoteAvailable;

}
