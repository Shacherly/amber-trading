package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货市场返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotMarketRes {
    @ApiModelProperty(value = "24小时涨跌", example = "34.23%")
    private String change24h;
    @ApiModelProperty(value = "价格", example = "23.02")
    private String price;
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "是否收藏", example = "true")
    private Boolean favorite;
}
