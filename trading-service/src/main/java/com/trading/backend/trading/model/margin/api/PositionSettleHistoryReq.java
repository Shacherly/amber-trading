package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adam.wang
 * @date 2021/10/8 18:18
 */
@Data
@ApiModel(value = "交割历史请求")
@RequestUnderlineToCamel
public class PositionSettleHistoryReq extends PageTimeRangeReq {
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    
    @Pattern(regexp = "BUY|SELL")
    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;
}
