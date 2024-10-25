package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货下单请求返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotOrderPlaceRes {

    @ApiModelProperty(value = "订单id", example = "21312")
    private String orderId;
    @ApiModelProperty(value = "base 交易数量", example = "21312")
    private BigDecimal baseFilled;
    @ApiModelProperty(value = "quote 交易数量", example = "21312")
    private BigDecimal quoteFilled;
    @ApiModelProperty(value = "成交价格", example = "21312")
    private BigDecimal filledPrice;
    @ApiModelProperty(value = "状态", example = "COMPLETE")
    private String status;
    @JsonIgnore
    private String originalStatus;
    @ApiModelProperty(value = "是否按照quote数量成交，否则是base数量成交", example = "true")
    private boolean isQuote;
    @ApiModelProperty(value = "委托数量中已成交数量", example = "21312")
    private BigDecimal quantityFilled;
    @ApiModelProperty(value = "交易数量中已成交数量", example = "21312")
    private BigDecimal amountFilled;
    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
}
