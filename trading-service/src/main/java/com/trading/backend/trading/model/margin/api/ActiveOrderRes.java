package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author trading
 * @date 2021/9/28 21:03
 */
@Data
@ApiModel(value = "杠杆当前委托列表响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActiveOrderRes {
	@ApiModelProperty(value = "订单号", example = "32323m323")
	private String orderId;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "订单类型 LIMIT MARKET STOP_LIMIT STOP_MARKET", example = "LIMIT")
	private String type;

	@ApiModelProperty(value = "订单状态 PRE_TRIGGER 待触发, EXECUTING 挂单中, EXCEPTION 异常, CANCELED 已取消, PART_CANCELED 部分成交取消, COMPLETED 完全成交", example = "EXECUTING")
	private String status;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "委托数量", example = "10")
	private BigDecimal quantity;

	@ApiModelProperty(value = "委托价", example = "50000")
	private BigDecimal price;

	@ApiModelProperty(value = "触发价", example = "50000")
	private BigDecimal triggerPrice;

	@ApiModelProperty(value = "大于/小于", example = "<")
	private String triggerCompare;

	@ApiModelProperty(value = "开仓价格", example = "5000")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "成交均价", example = "100.21")
	private BigDecimal filledPrice;

	@ApiModelProperty(value = "成交数量", example = "100.21")
	private BigDecimal quantityFilled;

	@ApiModelProperty(value = "委托时间", example = "1632835745000")
	private Date ctime;

	@ApiModelProperty(value = "提示信息", example = "1632835745000")
	private String notes;
}
