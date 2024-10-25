package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.backend.trading.config.web.StringLowerCaseSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "openapi平仓响应数据")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseVo {

    @ApiModelProperty(value = "orderId", example = "12312")
    private String orderId;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    @JsonProperty("contract")
    private String symbol;

    @ApiModelProperty(value = "open, close", example = "close")
    private final String openClose = "close";

    @ApiModelProperty(value = "limit-gtc,limit-fak,limit-fok,market", example = "market")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private final String orderType = "market";

    @ApiModelProperty(value = "方向,buy 买，sell", notes="buy 买，sell卖", example = "buy")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String direction;

    @ApiModelProperty(value = "成交价格，和avg_price一致", example = "50")
    private BigDecimal price;

    @ApiModelProperty(value = "委托平仓数量", example = "50")
    private BigDecimal quantity;

    @ApiModelProperty(value = "pending,ing,canceled,finished", example = "finished")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String fillStatus;

    @ApiModelProperty(value = "订单状态 pending 待触发, new 执行中, parted 部分成交, filled 已成交, canceled 已取消", example = "filled")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String status;

    @ApiModelProperty(value = "委托平仓成交数量", example = "BTC_USD")
    @JsonProperty("filled")
    private BigDecimal filledQuantity;

    @ApiModelProperty(value = "成交均价", example = "20000")
    @JsonProperty("avg_price")
    private BigDecimal filledPrice;

    @ApiModelProperty(value = "订单创建时间", example = "1634285709000")
    @JsonProperty("created_time")
    private long ctime;

    @ApiModelProperty(value = "订单更新时间", example = "1634285709000")
    @JsonProperty("updated_time")
    private long mtime;
}
