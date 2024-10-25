package com.google.backend.trading.model.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "交易记录1")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransactionInfoResEvent {
    @ApiModelProperty(value = "交易Id", example = "12312")
    private String transactionId;

    @ApiModelProperty(value = "仓位Id", example = "12312")
    private String positionId;

    @ApiModelProperty(value = "订单类型:LIMIT 限价,MARKET 市价,STOP_LIMIT 触发限价,STOP_MARKET 触发市价", notes = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", example = "LIMIT")
    private String orderType;

    @ApiModelProperty(value = "交易类型：MARGIN 杠杆 SPOT 现货", example = "SPOT")
    private String transactionType;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向：BUY、SELL", notes = "方向：BUY、SELL", example = "1")
    private String direction;

    @ApiModelProperty(value = "base数量", example = "50")
    private BigDecimal baseQuantity;

    @ApiModelProperty(value = "quote数量", example = "50")
    private BigDecimal quoteQuantity;

    @ApiModelProperty(value = "价格", example = "50000")
    private BigDecimal price;

    @ApiModelProperty(value = "收益", example = "300")
    private BigDecimal pnl;

    @ApiModelProperty(value = "收益币种", example = "USD")
    private String pnlCoin;

    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;

    @ApiModelProperty(value = "时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;

    @ApiModelProperty(value = "操作类型: 杠杆（ OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓 REDUCE_POSITION 减仓 SETTLE_POSITION 交割）; 现货（ PLACED_BY_CLIENT 用户主动发生 LIQUIDATION 清算发生 AUTO_CONVERSION 盈亏转换发生 PLACED_BY_API API用户发生 FIX_NEGATIVE 平负余额发生 OTC_SHOP BOOKING用户发生 LOAN_LIDQUIDATION 借贷发生 REPAY_WITH_COLLATERAL 借贷质押还款 FORCE_CLOSE 强平发生）", example = "USD")
    private String optType;
}
