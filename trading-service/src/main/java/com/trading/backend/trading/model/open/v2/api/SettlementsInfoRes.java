package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@ApiModel(value = "v2-openapi仓位交割响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SettlementsInfoRes {

	@ApiModelProperty(value = "仓位id", example = "123123123")
	private String positionId;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "可用余额", dataType = "com.google.backend.trading.model.open.v2.api.SettlementsInfoRes$AvailableBalanceDemo")
	private Map<String, BigDecimal> availableBalance;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "0.123")
	private BigDecimal quantity;

	@ApiModelProperty(value = "开仓均价", example = "0.123")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "最大可交割", example = "0.123")
	private BigDecimal maxSettle;

	@Data
	@ApiModel(value = "AvailableBalanceDemo")
	public static class AvailableBalanceDemo {

		@ApiModelProperty(value = "余额", example = "0.123")
		private String btc;

		public static void main(String[] args) {
			System.err.println(AvailableBalanceDemo.class.getName());
		}
	}
}
