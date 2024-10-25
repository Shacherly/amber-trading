package com.google.backend.trading.model.commonconfig.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 暂时没有列举所有的配置，只获取需要的配置
 *
 * @author trading
 * @date 2021/10/9 11:34
 */
@Data
@ApiModel(value = "所有配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AllConfig {

	@ApiModelProperty(value = "通用配置", notes = "key 币种 BTC")
	private Map<String, CoinCommonConfig> common;

	@ApiModelProperty(value = "币对配置", notes = "key 币对 BTC_USD")
	private Map<String, CoinSymbolConfig> symbol;

	@ApiModelProperty(value = "兑换配置", notes = "key 币种 BTC")
	private Map<String, CoinSwapConfig> swap;
}
