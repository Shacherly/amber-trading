package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.backend.trading.config.web.I18nConvertSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:52
 */
@ApiModel(value = "WEB 快捷下单返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuickSpotOrderPlaceRes {
    @ApiModelProperty(value = "订单id", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @ApiModelProperty(value = "支付币种", example = "BTC")
    private String fromCoin;

    @ApiModelProperty(value = "获得币种", example = "USD")
    private String toCoin;

    @ApiModelProperty(value = "支付数量", example = "1231")
    private BigDecimal fromQuantity;

    @ApiModelProperty(value = "成交数量", example = "123213")
    private BigDecimal toQuantity;

    @ApiModelProperty(value = "成交汇率",  example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "订单状态: PRE_TRIGGER, EXECUTING, LOCKED, COMPLETED, CANCELED",
            notes = "1 预触发 2 执行中 3 已完成 4 已取消", example = "EXECUTING")
    private String status;

    @ApiModelProperty(value = "备注", example = "Test")
    @JsonSerialize(using = I18nConvertSerializer.class)
    private String memo;

    @ApiModelProperty(value = "创建时间", notes="13位毫秒时间戳" ,example = "1633425311991")
    private Long ctime;
}
