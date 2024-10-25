package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 20:54
 */
@Data
@ApiModel(value = "杠杆下单确认响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderConfirmRes {

	@ApiModelProperty(value = "预估手续费", example = "30.12")
	private BigDecimal fee;

	@ApiModelProperty(value = "手续费率 计算公式：数量 * usdPrice * feeRate", example = "0.0005")
	private BigDecimal feeRate;

	@ApiModelProperty(value = "base对usd的价格", example = "50000")
	private BigDecimal baseUsdPrice;
	// TODO
	@ApiModelProperty(value = "quote对usd的价格", example = "50000")
	private BigDecimal quoteUsdPrice;
}
