package com.google.backend.trading.model.spot.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author david.chen
 * @date 2021/12/28 14:44
 */
@Data
@ApiModel(value = "Web 快捷下单信息请求")
public class QuickSpotInfoReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "交易方向", required = true, example = "BUY")
    private String direction;
}
