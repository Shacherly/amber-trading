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

/**
 * @author trading
 * @date 2021/10/15 20:57
 */
@Data
@ApiModel(value = "openapi平仓交易记录响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClosePositionTransactionVo {

	@ApiModelProperty(value = "开仓价格", example = "50000")
	@JsonProperty("entry_price")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "平仓价格", example = "50000")
	@JsonProperty("price")
	private BigDecimal closePrice;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	@JsonProperty("contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "方向", example = "BUY")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String direction;

	@ApiModelProperty(value = "平仓数量", example = "1.5")
	private BigDecimal size;

	@ApiModelProperty(value = "平仓时间", example = "1632835745000")
	@JsonProperty("created_time")
	private Long ctime;

	@ApiModelProperty(value = "盈亏", example = "15")
	private BigDecimal pnl;

	@ApiModelProperty(value = "盈亏币种", example = "USD")
	@JsonProperty("pnl_unit")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String pnlCoin;


}
