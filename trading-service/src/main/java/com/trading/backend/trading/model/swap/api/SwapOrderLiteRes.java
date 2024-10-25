package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:52
 */
@ApiModel(value = "lite版本兑换订单返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapOrderLiteRes {
    @ApiModelProperty(value = "订单id", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @ApiModelProperty(value = "支付币种", example = "BTC")
    private String fromCoin;

    @ApiModelProperty(value = "获得币种", example = "USD")
    private String toCoin;

    @ApiModelProperty(value = "支付数量", example = "1231")
    private BigDecimal fromQuantity;
    /**
     * 兑换方式 1: 委托支付（固定支付）  2：委托获得（固定获得）
     */
    @ApiModelProperty(hidden = true)
    private String mode;

    @ApiModelProperty(value = "成交数量", example = "123213")
    private BigDecimal toQuantity;

    @ApiModelProperty(value = "成交汇率", example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "订单状态: EXECUTING, COMPLETED, CANCELED",
            notes = "EXECUTING 执行中 COMPLETED 已完成 CANCELED 已取消", example = "EXECUTING")
    private String status;

    @ApiModelProperty(value = "备注 错误信息", example = "Test")
    private String memo;

    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Long ctime;

    @ApiModelProperty(value = "订单来源 AUTOMATIC_INVESTMENT_PLAN 表示定投", example = "AUTOMATIC_INVESTMENT_PLAN")
    private String source;
}
