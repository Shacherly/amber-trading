package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 20:45
 */
@Data
@ApiModel(value = "杠杆下单确认请求")
@RequestUnderlineToCamel
public class MarginOrderConfirmReq {

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
	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	private BigDecimal price;
}
