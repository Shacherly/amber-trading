package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.config.web.StringUpperCaseDeserializer;
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
@ApiModel(value = "openapi订单下单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderPlaceReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
	@JsonProperty(value = "contract")
	@JsonDeserialize(using = StringUpperCaseDeserializer.class)
	private String symbol;

	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	@Positive
	@Max(value = Long.MAX_VALUE)
	private BigDecimal price;

	@NotNull
	@ApiModelProperty(value = "委托数量", required = true, example = "10")
	@JsonProperty(value = "size")
	@Positive
	@Digits(integer = 32, fraction = 16)
	private BigDecimal quantity;

	@NotBlank
	@ApiModelProperty(value = "方向", required = true, example = "buy")
	@JsonDeserialize(using = StringUpperCaseDeserializer.class)
	private String direction;

	@NotBlank
	@ApiModelProperty(value = "limit-gtc,limit-fak,limit-fok,market", required = true, example = "market")
	@JsonDeserialize(using = StringUpperCaseDeserializer.class)
	private String type;

	@ApiModelProperty(value = "是否为现货", example = "true")
	private boolean isSpot;
}
