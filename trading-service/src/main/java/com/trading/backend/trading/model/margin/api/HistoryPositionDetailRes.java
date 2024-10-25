package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author trading
 * @date 2021/9/28 21:03
 */
@Data
@ApiModel(value = "历史仓位列表响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HistoryPositionDetailRes {

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "方向,BUY SELL", example = "BUY")
	private String direction;

	@ApiModelProperty(name = "max_quantity", value = "历史最高持仓数量", example = "10")
	private BigDecimal maxQuantity;

	@ApiModelProperty(value = "实现盈亏", example = "-56")
	private BigDecimal pnl;

	@ApiModelProperty(value = "开仓时间", example = "1632835745000")
	private Date ctime;

}
