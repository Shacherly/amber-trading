package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/23 21:03
 */
@Data
@ApiModel(value = "v2-openapi平仓请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
	private String symbol;

	@NotBlank
	@ApiModelProperty(value = "订单类型", example = "MARKET", required = true, allowableValues = "LIMIT,MARKET")
	private String orderType;

	@Positive
	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	private BigDecimal price;

	@ApiModelProperty(value = "执行策略", example = "GTC", allowableValues = "GTC,IOC,FOK")
	private String strategy;
}
