package com.google.backend.trading.model.common.model.riskcontrol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.common.dto.base.MetaDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/2 16:27
 */
@ApiModel(value = "仓位返回")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionRes extends MetaDto {

    /**
     * 用户uid
     */
    @ApiModelProperty(value = "用户id", example = "21312")
    private String uid;

    /**
     * 仓位id
     */
    @ApiModelProperty(value = "仓位id", example = "21312")
    private Long positionId;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对", example = "BTC_USD")
    private String symbol;

    /**
     * 仓位大小
     */
    @ApiModelProperty(value = "仓位大小", example = "21312")
    private BigDecimal size;

    /**
     * 仓位均价
     */
    @ApiModelProperty(value = "仓位均价", example = "21312")
    private BigDecimal price;

    /**
     * 方向 'sell', 'buy'
     */
    @ApiModelProperty(value = "方向 'sell', 'buy'", example = "sell")
    private String direction;

    /**
     * take profit 止盈
     */
    @ApiModelProperty(value = "止盈", example = "21312")
    private BigDecimal tp;
    /**
     * stop lose 止损
     */
    @ApiModelProperty(value = "止损", example = "21312")
    private BigDecimal sl;

}
