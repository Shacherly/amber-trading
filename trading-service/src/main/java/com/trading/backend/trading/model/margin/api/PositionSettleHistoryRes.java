package com.google.backend.trading.model.margin.api;

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
@ApiModel(value = "交割记录")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionSettleHistoryRes {
    @ApiModelProperty(value = "交易Id", example = "12312")
    private String transactionId;

    @ApiModelProperty(value = "仓位Id", example = "12312")
    private String positionId;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向:BUY、SELL", notes = "1 买，2卖", example = "1")
    private String direction;

    @ApiModelProperty(value = "数量", example = "50")
    private BigDecimal quantity;

    @ApiModelProperty(value = "价格", example = "50000")
    private BigDecimal price;

    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;

    @ApiModelProperty(value = "手续费币种", example = "USD")
    private String feeCoin;

    @ApiModelProperty(value = "时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
}
