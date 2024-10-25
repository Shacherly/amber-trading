package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/15 16:23
 */
@Data
@ApiModel(value = "v2-openapi订单信息响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderInfoVo {

	@ApiModelProperty(value = "订单id", example = "123123")
	private String orderId;

	@ApiModelProperty(value = "LIMIT,MARKET", example = "MARKET")
	private String orderType;

	@ApiModelProperty(value = "GTC,IOC,FOK", example = "GTC")
	private String strategy;

	@ApiModelProperty(value = "币对", example = "ETH_USD")
	private String symbol;

	@ApiModelProperty(value = "是否为quote下单 (SPOT单)", example = "false")
	private Boolean isQuote;

	@ApiModelProperty(value = "方向 BUY,SELL", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "委托价格", notes = "for limit单", example = "1231.2")
	private BigDecimal price;

	@ApiModelProperty(value = "委托数量",
			notes = "base数量成交即base数量，quote数量成交即quote数量", example = "1231")
	private BigDecimal quantity;

	@ApiModelProperty(value = "订单状态 PRE_TRIGGER(待触发) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) PART_CANCELED(部分成交取消)",
			example = "EXECUTING")
	private String status;

	@ApiModelProperty(value = "成交数量", example = "123213")
	private BigDecimal filledQuantity;

	@ApiModelProperty(value = "成交价格", example = "4321")
	private BigDecimal filledPrice;

	@ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
	private Long ctime;

	@ApiModelProperty(value = "订单更新时间", example = "1634285709000")
	private Long mtime;
}
