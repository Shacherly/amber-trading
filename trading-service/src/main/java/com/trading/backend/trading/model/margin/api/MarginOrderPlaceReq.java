package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 20:45
 */
@Data
@ApiModel(value = "杠杆下单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderPlaceReq {

	@NotBlank
	@ApiModelProperty(value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
			example = "MARKET")
	private String type;

	@ApiModelProperty(value = "订单策略:FOK,IOC,GTC", allowableValues = "FOK,IOC,GTC", example = "FOK")
	private String strategy = "GTC";

	@NotBlank
	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@NotBlank
	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@NotNull
	@Positive
	@ApiModelProperty(value = "委托数量", required = true, example = "10")
	@Digits(integer = 32, fraction = 16)
	private BigDecimal quantity;

	@Positive
	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	@Max(value = Long.MAX_VALUE)
	private BigDecimal price;

	@Positive
	@ApiModelProperty(name = "trigger_price", value = "触发价", example = "20000")
	@Max(value = Long.MAX_VALUE)
	private BigDecimal triggerPrice;

	@Pattern(regexp = ">|<")
	@ApiModelProperty(name = "trigger_compare", value = "大于/小于触发价", allowableValues = ">,<", example = ">")
	private String triggerCompare;

	@ApiModelProperty(name = "reduce_only", value = "只减仓，默认false", example = "false")
	private boolean reduceOnly = false;

	@ApiModelProperty(value = "备注", example = "梭哈抄底")
	private String notes;
}
