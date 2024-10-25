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
@ApiModel(value = "openapi交易记录信息响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransactionVo {

	@ApiModelProperty(value = "订单id", example = "123123")
	@JsonProperty("trade_id")
	private String transactionId;

	@ApiModelProperty(value = "币对", example = "ETH_USD")
	@JsonProperty(value = "contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "方向,buy 买，sell", notes="buy 买，sell卖", example = "buy")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String direction;

	@ApiModelProperty(value = "委托价格", notes="limit单传入", example = "1231.2")
	private BigDecimal price;

	@ApiModelProperty(value = "成交数量", example = "4321")
	@JsonProperty(value = "amount")
	private BigDecimal quantity;

	@ApiModelProperty(value = "创建时间", notes="13位毫秒时间戳" ,example = "1633425311991")
	@JsonProperty(value = "created_time")
	private Long ctime;

	@ApiModelProperty(value = "open, close, spot", example = "spot")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String type;

	@ApiModelProperty(value = "limit-gtc,limit-fak,limit-fok,market", example = "market")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String orderType;

	@ApiModelProperty(value = "订单id", example = "uuid")
	private String orderId;
}
