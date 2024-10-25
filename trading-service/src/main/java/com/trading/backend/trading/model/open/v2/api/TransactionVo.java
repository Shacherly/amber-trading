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
@ApiModel(value = "v2-openapi交易记录信息响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransactionVo {

	@ApiModelProperty(value = "订单id, for MARGIN, SPOT", example = "uuid")
	private String orderId;

	@ApiModelProperty(value = "订单类型:LIMIT 限价,MARKET 市价, for MARGIN, SPOT", example = "LIMIT")
	private String orderType;

	@ApiModelProperty(value = "交易id", example = "123123")
	private String transactionId;

	@ApiModelProperty(value = "仓位id, for MARGIN, SETTLE", example = "123123")
	private String positionId;

	@ApiModelProperty(value = "币对", example = "ETH_USD")
	private String symbol;

	@ApiModelProperty(value = "方向,BUY 买，SELL", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "委托价格", notes = "limit单传入", example = "1231.2")
	private BigDecimal price;

	@ApiModelProperty(value = "成交数量", example = "4321")
	private BigDecimal quantity;

	@ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1633425311991")
	private Long ctime;

	@ApiModelProperty(value = "MARGIN 杠杆, SPOT 现货, SETTLE 交割", example = "SPOT")
	private String type;

	@ApiModelProperty(value = "盈亏 ,for MARGIN", example = "10.11")
	private BigDecimal pnl;

	@ApiModelProperty(value = "盈亏币种,for MARGIN", example = "USD")
	private String pnlCoin;

	@ApiModelProperty(value = "手续费", example = "10.11")
	private BigDecimal fee;

	@ApiModelProperty(value = "手续费币种", example = "10.11")
	private BigDecimal feeCoin;

}
