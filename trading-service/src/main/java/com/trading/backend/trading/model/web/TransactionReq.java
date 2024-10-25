package com.google.backend.trading.model.web;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adam.wang
 */
@ApiModel(value = "交易搜索过滤条件")
@Data
@RequestUnderlineToCamel
public class TransactionReq extends PageTimeRangeReq {

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @Pattern(regexp = "BUY|SELL")
    @ApiModelProperty(value = "方向：BUY、SELL", example = "BUY")
    private String direction;
}
