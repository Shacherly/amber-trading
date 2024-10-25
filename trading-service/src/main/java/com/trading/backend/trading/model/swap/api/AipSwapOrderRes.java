package com.google.backend.trading.model.swap.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/2/7 15:48
 */
@ApiModel(value = "定投兑换订单返回")
@Data
public class AipSwapOrderRes {
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

    @ApiModelProperty(value = "成交汇率", example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "订单状态: EXECUTING, COMPLETED, CANCELED",
            notes = "EXECUTING 执行中 COMPLETED 已完成 CANCELED 已取消", example = "EXECUTING")
    private String status;

    @ApiModelProperty(value = "备注 CANCELED（取消）——memo记录原因(trading_swap_order_memo_no_balance、trading_swap_order_memo_timeout)", example = "Test")
    private String memo;

    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Long ctime;
}
