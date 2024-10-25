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
 * @author trading
 * @date 2021/10/15 15:00
 */
@Data
@ApiModel(value = "openapi下单响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderPlaceRes {

	@ApiModelProperty(value = "订单id", example = "uuid")
	private String orderId;

	@ApiModelProperty(value = "limit-gtc,limit-fak,limit-fok,market", example = "market")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String orderType;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	@JsonProperty("contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "委托价格", example = "1000")
	private BigDecimal price;

	@ApiModelProperty(value = "委托数量", example = "0.01")
	private BigDecimal quantity;

	@ApiModelProperty(value = "订单状态 pending 待触发, new 执行中, parted 部分成交, filled 已成交, canceled 已取消", example = "filled")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String status;

	@ApiModelProperty(value = "订单状态 pending 待触发, ing 执行中, finished 完成, canceled 已取消", example = "finished")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String fillStatus;

	@ApiModelProperty(value = "是否是现货", example = "true")
	private Boolean isSpot;

	@ApiModelProperty(value = "成交数量", example = "10")
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
