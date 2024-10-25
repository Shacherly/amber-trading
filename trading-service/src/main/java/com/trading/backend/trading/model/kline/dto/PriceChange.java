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
public class PriceChange {

	@ApiModelProperty(value = "币对")
	private String symbol;

	@ApiModelProperty(value = "实时价格")
	private BigDecimal price;

	@ApiModelProperty(value = "n天前的历史价格")
	private BigDecimal priceOld;

	@ApiModelProperty(value = "实时价格的更新时间")
	private Long timestamp;

	@ApiModelProperty(value = "n天前")
	private Integer days;
}
