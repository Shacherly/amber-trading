package com.google.backend.trading.model.spot.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货市场收藏变更")
@Data
public class SpotMarketMarkReq {
    @NotNull(message = "symbol must not be null")
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;
    @NotNull(message = "favorite must not be null")
    @ApiModelProperty(value = "是否收藏",required = true,  example = "true")
    private Boolean favorite;
}
