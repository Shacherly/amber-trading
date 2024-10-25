package com.google.backend.trading.model.margin.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 21:01
 */
@Data
@ApiModel(value = "实时询价请求")
public class PriceReq {

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@Positive
	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(value = "数量", example = "200")
	private BigDecimal quantity;

	@NotBlank
	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;


}
