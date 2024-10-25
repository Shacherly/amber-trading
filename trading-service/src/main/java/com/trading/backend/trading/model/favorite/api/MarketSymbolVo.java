package com.google.backend.trading.model.favorite.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/16 17:00
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarketSymbolVo {

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "顺序优先级", example = "100")
	private Integer priority;

	@ApiModelProperty(value = "价格", example = "50012.25")
	private BigDecimal price = BigDecimal.ZERO;

	@ApiModelProperty(value = "是否收藏", example = "true")
	private boolean favorite = false;

	private Integer favoritePriority = Integer.MAX_VALUE;

	@ApiModelProperty(value = "24小时涨跌", example = "0.23")
	private BigDecimal change24h = BigDecimal.ZERO;

	@ApiModelProperty(value = "市场分类")
	private String marketCategory;
}
