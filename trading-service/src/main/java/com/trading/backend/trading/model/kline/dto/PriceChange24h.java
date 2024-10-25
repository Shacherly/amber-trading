package com.google.backend.trading.model.kline.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/18 19:00
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PriceChange24h {

	@ApiModelProperty(value = "币对")
	private String symbol;

	@ApiModelProperty(value = "实时价格")
	private BigDecimal price;

	@ApiModelProperty(value = "24小时前的价格")
	private BigDecimal priceOld;

	@ApiModelProperty(value = "实时价格的更新时间")
	private Long timestamp;
}
