package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仓位数据
 * @author adam.wang
 * @date 2021/9/28 19:48
 */
@ApiModel(value = "仓位数据")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionInfoRes {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "1032b321")
    private String uid;
    /**
     * 仓位记录id
     */
    @ApiModelProperty(name="position_id", value = "仓位记录id", example = "201")
    private String positionId;

    /**
     * 合约类型
     */
    @ApiModelProperty(value = "合约类型：MARGIN（预留默认未MARGIN）", example = "MARGIN")
    private String type = "MARGIN";

    /**
     * 总量
     */
    @ApiModelProperty(value = "总量", example = "10.0")
    private BigDecimal quantity;


    /**
     * 持仓方向
     */
    @ApiModelProperty(value = "持仓方向：BUY, SELL", notes="方向 BUY, SELL", example = "BUY")
    private String direction;

    /**
     * 持仓均价
     */
    @ApiModelProperty(name="avg_price",value = "持仓均价", example = "3000.3")
    private BigDecimal avgPrice;


    /**
     * 合约币种
     */
    @ApiModelProperty(value = "合约币种", example = "BTC_USDT")
    private String symbol;


    /**
     * 浮动盈亏（usd计价）
     */
    @ApiModelProperty(value = "浮动盈亏（usd计价）", example = "12.2")
    private BigDecimal pnl;

    /**
     * 浮动盈亏币种，为了amp使用方便默认返回USD
     */
    @ApiModelProperty(value = "浮动盈亏币种", example = "USD")
    private String pnlCoin = "USD";

}
