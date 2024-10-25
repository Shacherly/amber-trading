package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 20:45
 */
@Data
@ApiModel(value = "v2-openapi现货下单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotPlaceReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	@Positive
	private BigDecimal price;

	@NotNull
	@ApiModelProperty(value = "委托数量", required = true, example = "10")
	@Positive
	@Digits(integer = 32, fraction = 16)
	private BigDecimal quantity;

	@NotBlank
	@ApiModelProperty(value = "方向", required = true, example = "BUY")
	private String direction;

	@NotBlank
	@ApiModelProperty(value = "LIMIT,MARKET", required = true, example = "MARKET")
	private String type;

	@ApiModelProperty(value = "策略， GTC,IOC,FOK  for LIMIT单", example = "GTC")
	private String strategy;
}
