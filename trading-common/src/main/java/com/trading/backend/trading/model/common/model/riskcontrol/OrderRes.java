package com.google.backend.trading.model.common.model.riskcontrol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/2 16:26
 */
@ApiModel(value = "订单返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderRes {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "21312")
    private String uid;
    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单编号", example = "21a312")
    private Long orderId;
    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对", example = "BYC_USD")
    private String symbol;
    /**
     * 未成交数量
     */
    @ApiModelProperty(value = "未成交数量", example = "321")
    private BigDecimal size;
    /**
     * 挂单价
     */
    @ApiModelProperty(value = "挂单价", example = "321")
    private BigDecimal price;

    /**
     * 方向 'sell', 'buy'
     */
    @ApiModelProperty(value = "方向 'SELL', 'BUY'", example = "SELL")
    private String direction;

}
