package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/15 15:00
 */
@Data
@ApiModel(value = "v2-openapi现货下单响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotPlaceRes {

	@ApiModelProperty(value = "订单id", example = "uuid")
	private String orderId;

	@ApiModelProperty(value = "LIMIT,MARKET", example = "MARKET")
	private String orderType;

	@ApiModelProperty(value = "GTC,IOC,FOK", example = "GTC")
	private String strategy;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "是否为quote下单", example = "false")
	private Boolean isQuote;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "委托价格", example = "1000")
	private BigDecimal price;

	@ApiModelProperty(value = "委托数量", example = "0.01")
	private BigDecimal quantity;

	@ApiModelProperty(value = "订单状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) COMPLETED(完全成交) CANCELED(完全取消) " +
			"PART_CANCELED(部分成交取消)", example = "EXECUTING")
	private String status;

	@ApiModelProperty(value = "成交数量", example = "10")
	private BigDecimal filledQuantity;

	@ApiModelProperty(value = "成交均价", example = "20000")
	private BigDecimal filledPrice;

	@ApiModelProperty(value = "订单创建时间", example = "1634285709000")
	private Long ctime;

	@ApiModelProperty(value = "订单更新时间", example = "1634285709000")
	private Long mtime;
}
