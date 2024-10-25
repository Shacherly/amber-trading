package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adam.wang
 * @date 2021/9/28 15:48
 */
@ApiModel(value = "兑换历史列表请求对象")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapOrderHistoryReq extends PageTimeRangeReq {

    @ApiModelProperty(value = "币种", example = "USD")
    private String coin;

    @ApiModelProperty(value = "状态", example = "COMPLETED|CANCELED")
    @Pattern(regexp = "COMPLETED|CANCELED", message = "状态错误")
    private String status;
}
