package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.backend.trading.config.web.StringLowerCaseSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author trading
 * @date 2021/9/28 22:11
 */
@Data
@ApiModel(value = "openapi在持仓位信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivePositionVo {

	@ApiModelProperty(value = "方向", example = "buy")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "10")
	private BigDecimal quantity;

	@ApiModelProperty(value = "开仓价格", example = "5000")
	private BigDecimal price;

	@ApiModelProperty(value = "预计盈亏单位", example = "USD")
	@JsonProperty(value = "pnl_unit")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String pnlCoin;

	@ApiModelProperty(value = "盈亏", example = "-56")
	private BigDecimal pnl;

	@ApiModelProperty(value = "占用保证金", example = "50000")
	@JsonProperty(value = "margin")
	private BigDecimal usedMargin;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	@JsonProperty(value = "contract")
	@JsonSerialize(using = StringLowerCaseSerializer.class)
	private String symbol;

	@ApiModelProperty(value = "更新时间", example = "1634194831724")
	@JsonProperty(value = "update_time")
	private Long mtime;

	@ApiModelProperty(value = "币对资金费率")
	@JsonProperty(value = "fund_rate")
	private SymbolFundingRate symbolFundingRate;

	@ApiModelProperty(value = "预计资金费用", example = "5.123")
	@JsonProperty(value = "estimated_fcost")
	private BigDecimal expectedFundingCost;

	@ApiModelProperty(value = "持仓头寸", example = "51231.2")
	private BigDecimal position;

	@ApiModelProperty(value = "持仓数量（冗余字段）", example = "51231.2")
	private BigDecimal size;

	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class SymbolFundingRate extends HashMap<String, Object> {

		public SymbolFundingRate(String base, FundingRate baseFundingRate, String quote, FundingRate quoteFundingRate, Long time) {
			super();
			put(base.toLowerCase(), baseFundingRate);
			put(quote.toLowerCase(), quoteFundingRate);
			put("timestamp",time);
		}
	}

	@Data
	@AllArgsConstructor
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class FundingRate {

		private BigDecimal lend;

		private BigDecimal borrow;
	}

}
