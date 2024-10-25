package com.google.backend.trading.model.swap.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author adam.wang
 * @date 2021/9/28 14:42
 */
@Data
@ApiModel(value = "快捷兑换信息请求")
public class QuickSwapInfoReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "交易方向", required = true, example = "BUY")
    private String direction;

}
