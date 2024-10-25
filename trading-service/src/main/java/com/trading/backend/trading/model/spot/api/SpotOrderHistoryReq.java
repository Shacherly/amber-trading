package com.google.backend.trading.model.spot.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货委托活跃列表请求对象")
@Data
@Valid
@RequestUnderlineToCamel
public class SpotOrderHistoryReq extends PageTimeRangeReq {

    @ApiModelProperty(value = "币对", required = false, example = "BTC_USD")
    private String symbol;

    @NotBlank(message = "order_item must not be blank")
    @ApiModelProperty(name = "order_item", value = "排序类型,SYMBOL币对 TIME成交时间 CTIME创建时间",
            notes = "SYMBOL币对 TIME成交时间 CTIME创建时间", required = true, example = "SYMBOL")
    @Pattern(regexp = "SYMBOL|TIME|CTIME")
    private String orderItem;

    @NotBlank(message = "order_mode must not be blank")
    @ApiModelProperty(name = "order_mode", value = "排序类型,ASC升序 DESC降序",
            notes = "ASC升序 DESC降序", required = true, example = "ASC")
    @Pattern(regexp = "ASC|DESC")
    private String orderMode;

    @ApiModelProperty(value = "方向", notes = "bug 买，SELL卖", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "订单状态", allowableValues = "COMPLETED(全部成交),CANCELED(取消)", example = "COMPLETED")
    @Pattern(regexp = "COMPLETED|CANCELED")
    private String status;

}
