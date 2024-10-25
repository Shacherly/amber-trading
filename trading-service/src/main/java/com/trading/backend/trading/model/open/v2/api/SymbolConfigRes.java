package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author trading
 * @date 2021/10/15 15:00
 */
@Data
@ApiModel(value = "v2-openapi币种配置响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SymbolConfigRes {

	@ApiModelProperty(value = "配置", dataType = "com.google.backend.trading.model.open.v2.api.SymbolConfigRes$SymbolConfigResConfigDemo")
	private Map<String, Config> config;

	@Data
	@ApiModel(value = "v2-openapi币种配置子元素")
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Config {

		@ApiModelProperty(value = "最小下单量", example = "1")
		private BigDecimal orderMinimum;

		@ApiModelProperty(value = "最大下单量", example = "10000")
		private BigDecimal orderMaximum;

		@ApiModelProperty(value = "fok最大下单量", example = "1000")
		private BigDecimal fokOrderMaximum;

		@ApiModelProperty(value = "base精度", example = "6")
		private int baseScale;

		@ApiModelProperty(value = "价格精度", example = "2")
		private int priceScale;

		private final boolean supportFok = true;

		public Config(CoinSymbolConfig symbolConfig, CoinCommonConfig commonConfig) {
			this.orderMinimum = symbolConfig.getMinOrderAmount();
			this.orderMaximum = symbolConfig.getMaxOrderAmount();
			this.fokOrderMaximum = symbolConfig.getFokMaxOrderAmount();
			this.priceScale = symbolConfig.getPrecision();
			this.baseScale = commonConfig.getBaseIssueQuantity();
		}

	}

	@Data
	@ApiModel(value = "SymbolConfigResConfigDemo")
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class SymbolConfigResConfigDemo {

		@ApiModelProperty(value = "配置")
		private Config btcUsd;
	}
}
