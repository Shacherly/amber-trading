package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易流水数据
 * @author adam.wang
 * @date 2021/9/29 10:47
 */
@ApiModel(value = "交易流水数据")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionFlowDetailRes {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "1032b321")
    private String uid;

    /**
     * 订单id
     */
    @ApiModelProperty(name="order_id", value = "订单id", example = "10b1")
    private String orderId;

    /**
     * 合约币种
     */
    @ApiModelProperty(value = "合约币种", example = "BTC_USDT")
    private String symbol;

    /**
     * 合约类型
     */
    @ApiModelProperty(name="order_type", value = "合约类型", example = "MARGIN")
    private String orderType="MARGIN";
    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型，LIMIT、MARKET、STOP-LIMIT、STOP-MARKET",
            notes="1: 限价单 2:市价单 3:限价条件单 4:市价条件单", example = "LIMIT")
    private String type;
    /**
     * 执行策略
     */
    @ApiModelProperty(value = "执行策略, GTC, IOC, FOK", notes="1:GTC 2:IOC 3:FOK", example = "GTC")
    private String strategy;

    /**
     * 交易方向
     */
    @ApiModelProperty(value = "交易方向 SELL、BUY", notes="方向 ELL、BUY", example = "BUY")
    private String direction;

    /**
     * 下单价格
     */
    @ApiModelProperty(value = "下单价格", example = "12.02")
    private BigDecimal price;

    /**
     * 下单数量
     */
    @ApiModelProperty(value = "下单数量", example = "12.02")
    private BigDecimal quantity;

    /**
     * 成交数量
     */
    @ApiModelProperty(name="quantity_filled", value = "成交数量", example = "12.02")
    private BigDecimal quantityFilled;
    /**
     * 成交均价
     */
    @ApiModelProperty(name="filled_price", value = "成交均价", example = "12.02")
    private BigDecimal filledPrice;

    /**
     * 订单状态
     */
    @ApiModelProperty(name="status", value = "订单状态PRE_TRIGGER PENDING EXECUTING LOCKED COMPLETED CANCELED CANCELING PART_CANCELED EXCEPTION（保证金不足，持仓上限等，具体原因会记录到error字段）", example = "12.02")
    private String status;
    /**
     * 创建时间
     */
    @ApiModelProperty(name="ctime", value = "创建时间", example = "1632883532072")
    private Date ctime;
    /**
     * 更新时间
     */
    @ApiModelProperty(name="mtime", value = "更新时间", example = "1632883532072")
    private Date mtime;
}
