package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/18 15:21
 */
@Data
@ApiModel(value = "现货可用")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class SpotAvailableVo {

    @ApiModelProperty(name="币种",  example = "USD")
    private String coin;

    @ApiModelProperty(name="余额",  example = "0.01")
    private BigDecimal balance =BigDecimal.ZERO;
}
