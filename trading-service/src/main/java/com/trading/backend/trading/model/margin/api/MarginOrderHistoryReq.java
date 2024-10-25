package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adam.wang
 * @date 2021/10/1 15:20
 */
@Data
@ApiModel(value = "杠杆历史订单请求对象")
@RequestUnderlineToCamel
public class MarginOrderHistoryReq extends PageTimeRangeReq {

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向", notes = "bug 买，SELL卖", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "排序字段", allowableValues = "CTIME,MTIME", example = "CTIME")
    @Pattern(regexp = "CTIME|MTIME")
    private String orderItem = "MTIME";

    @ApiModelProperty(value = "订单状态", allowableValues = "COMPLETED(全部成交),CANCELED(取消)", example = "COMPLETED")
    @Pattern(regexp = "COMPLETED|CANCELED")
    private String status;
}
