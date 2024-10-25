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
@ApiModel(value = "平仓记录历史")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseHistoryRes {
    @ApiModelProperty(name="transaction_id",value = "交易Id", example = "12312")
    private String transactionId;

    @ApiModelProperty(name="position_id",value = "仓位Id", example = "12312")
    private String positionId;

    @ApiModelProperty(value = "方向方向:BUY、SELL", notes = "1 买，2卖", example = "1")
    private String direction;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "成交类型 OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓  REDUCE_POSITION 减仓 SETTLE_POSITION 交割 SPOT 现货 PNL_CONVERSION 盈亏转换 LIQUIDATION 清算 FIX_NEGATIVE 平负余额", notes = "CLOSE_POSITION, REDUCE_POSITION", example = "CLOSE_POSITION")
    private String type;

    @ApiModelProperty(value = "数量", example = "50")
    private BigDecimal quantity;

    @ApiModelProperty(value = "成交价格", example = "50000")
    private BigDecimal price;

    @ApiModelProperty(name="open_price",value = "开仓价格", notes = "LIMIT单", example = "50000")
    private BigDecimal openPrice;

    @ApiModelProperty(value = "收益", example = "300")
    private BigDecimal pnl;

    @ApiModelProperty(name="pnl_coin",value = "收益币种", example = "USD")
    private String pnlCoin;

    @ApiModelProperty(value = "手续费", example = "100")
    private BigDecimal fee;

    @ApiModelProperty(name="fee_coin", value = "手续费币种", example = "USD")
    private String feeCoin;

    @ApiModelProperty(value = "时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
}
