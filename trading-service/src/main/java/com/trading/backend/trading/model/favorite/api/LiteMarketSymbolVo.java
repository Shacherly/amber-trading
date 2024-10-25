package com.google.backend.trading.model.favorite.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author david.chen
 * @date 2022/1/4 20:20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel("lite版本market")
@AllArgsConstructor
@NoArgsConstructor
public class LiteMarketSymbolVo {
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "是否置顶", example = "true")
    private boolean top = false;
}
