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
import java.util.Map;

/**
 * @author trading
 * @date 2021/10/15 17:54
 */
@Data
@ApiModel(value = "openapi仓位交割响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SettlementsInfoRes {

	@ApiModelProperty(value = "仓位id", example = "uuid")
	@JsonProperty("id")
	private String positionId;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	@JsonProperty("contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "可用余额", example = "0.123")
	@JsonProperty("balance")
	private Map<String, BigDecimal> availableBalance;

	@ApiModelProperty(value = "资金费率", example = "0.123")
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "0.123")
	private BigDecimal quantity;

	@ApiModelProperty(value = "开仓均价", example = "0.123")
	@JsonProperty("price")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "最大可交割", example = "0.123")
	private BigDecimal maxSettle;
}
