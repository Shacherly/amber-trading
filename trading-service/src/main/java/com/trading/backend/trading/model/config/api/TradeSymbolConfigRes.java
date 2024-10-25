package com.google.backend.trading.model.config.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/8 17:43
 */
@ApiModel(value = "币种配置响应数据")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeSymbolConfigRes {

	@ApiModelProperty(value = "币对", notes="币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "单次最小交易数量", notes="base 支付币种/quote 获得币种的价格", example = "2")
	private BigDecimal minSingle;

	@ApiModelProperty(value = "单次最大交易数量", notes="base 支付币种/quote 获得币种的价格", example = "200")
	private BigDecimal maxSingle;

	@ApiModelProperty(value = "精度", notes="精度", example = "8")
	private Integer precision;

	@ApiModelProperty(value = "类型", notes="类型", example = "2", allowableValues = "SPOT, MARGIN")
	private String type;

	@ApiModelProperty(value = "fok订单单次最大交易数量", notes="fok订单单次最大交易数量", example = "100")
	private BigDecimal fokMaxSingle;

}
