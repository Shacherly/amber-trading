package com.google.backend.trading.model.internal.aceup;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/9/28 14:52
 */
@ApiModel(value = "aceup-兑换返回对象")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AceUpSwapRes {
    @ApiModelProperty(value = "订单id", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @ApiModelProperty(value = "用户ID", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String uid;

    @ApiModelProperty(value = "支付币种", example = "BTC")
    private String fromCoin;

    @ApiModelProperty(value = "支付数量", example = "1231")
    private BigDecimal fromQuantity;

    @ApiModelProperty(value = "获得币种", example = "USD")
    private String toCoin;

    @ApiModelProperty(value = "成交数量", example = "123213")
    private BigDecimal toQuantity;

    @ApiModelProperty(value = "兑换方式 PAYMENT: 委托支付（固定支付）  OBTAINED：委托获得（固定获得） ", example = "123213")
    private String mode;

    @ApiModelProperty(value = "下单价格", example = "1231.2")
    private BigDecimal orderPrice;

    @ApiModelProperty(value = "成交汇率", example = "1231.2")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "手续费", example = "1231.2")
    private BigDecimal fee;

    @ApiModelProperty(value = "手续费币种", example = "ETH")
    private String feeCoin;

    @ApiModelProperty(value = "订单状态: EXECUTING, COMPLETED, CANCELED",
            notes = "EXECUTING 执行中 COMPLETED 已完成 CANCELED 已取消", example = "EXECUTING")
    private String status;

    @ApiModelProperty(value = "备注 错误信息", example = "Test")
    private String memo;

    @ApiModelProperty(value = "订单来源 PLACED_BY_CLIENT(用户下单) REPAY_WITH_COLLATERAL(借贷质押还款订单)AUTOMATIC_INVESTMENT_PLAN（定投）", example = "AUTOMATIC_INVESTMENT_PLAN")
    private String source;

    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Date ctime;

    @ApiModelProperty(value = "更新时间", notes = "13位毫秒时间戳", example = "1633425311991")
    private Date mtime;
}
