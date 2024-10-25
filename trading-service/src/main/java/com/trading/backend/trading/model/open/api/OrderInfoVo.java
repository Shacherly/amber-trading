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
 * @date 2021/10/15 16:23
 */
@Data
@ApiModel(value = "openapi订单信息响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderInfoVo {

	@ApiModelProperty(value = "订单id", example = "123123")
	private String orderId;

	@ApiModelProperty(value = "limit-gtc,limit-fak,limit-fok,market", example = "market")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String orderType;

	@ApiModelProperty(value = "委托价格", notes="limit单传入", example = "1231.2")
	private BigDecimal price;

	@ApiModelProperty(value = "成交价格", example = "4321")
	@JsonProperty(value = "avg_price")
	private BigDecimal filledPrice;

	@ApiModelProperty(value = "创建时间", notes="13位毫秒时间戳" ,example = "1633425311991")
	@JsonProperty(value = "created_time")
	private Long ctime;

	@ApiModelProperty(value = "方向,buy 买，sell", notes="buy 买，sell卖", example = "buy")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String direction;

	@ApiModelProperty(value = "成交数量", example = "123213")
	@JsonProperty(value = "filled")
	private BigDecimal filledQuantity;

	@ApiModelProperty(value = "委托数量",
			notes="base数量成交即base数量，quote数量成交即quote数量", example = "1231")
	private BigDecimal quantity;

	@ApiModelProperty(value = "订单状态 pending 待触发, new 执行中, parted 部分成交, filled 已成交, canceled 已取消", example = "filled")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String status;

	@ApiModelProperty(value = "pending,ing,canceled,finished", example = "finished")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String fillStatus;

	@ApiModelProperty(value = "币对", example = "ETH_USD")
	@JsonProperty(value = "contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "是否是现货", example = "true")
	private Boolean isSpot;

	@ApiModelProperty(value = "只减仓", example = "true")
	private Boolean reduceOnly;

	@ApiModelProperty(value = "是否可执行，保证金不足时为false，但任然是委托中的订单", example = "true")
	private boolean executable = true;
}
