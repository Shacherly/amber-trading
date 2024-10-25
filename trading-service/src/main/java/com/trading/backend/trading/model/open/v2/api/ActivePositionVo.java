package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@ApiModel(value = "v2-openapi在持仓位信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivePositionVo {

	@ApiModelProperty(value = "仓位ID", example = "423fd4545")
	private String positionId;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "10")
	private BigDecimal quantity;

	@ApiModelProperty(value = "开仓价格", example = "5000")
	private BigDecimal price;

	@ApiModelProperty(value = "预计盈亏单位", example = "USD")
	private String pnlCoin;

	@ApiModelProperty(value = "盈亏", example = "-56")
	private BigDecimal pnl;

	@ApiModelProperty(value = "未盈亏", example = "-56")
	private BigDecimal unpnl;

	@ApiModelProperty(value = "未实现盈亏单位", example = "USD")
	private String unpnlCoin;

	@ApiModelProperty(value = "占用保证金", example = "50000")
	private BigDecimal usedMargin;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "开仓时间", example = "1634194831724")
	private Long ctime;

	@ApiModelProperty(value = "更新时间", example = "1634194831724")
	private Long mtime;

	@ApiModelProperty(value = "币对资金费率", dataType = "com.google.backend.trading.model.open.v2.api.ActivePositionVo$SymbolFundingRateDemo")
	private SymbolFundingRate symbolFundingRate;

	@ApiModelProperty(value = "预计资金费用", example = "5.123")
	private BigDecimal expectedFundingCost;

	@ApiModelProperty(value = "持仓头寸", example = "51231.2")
	private BigDecimal position;

	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class SymbolFundingRate extends HashMap<String, Object> {

		public SymbolFundingRate(String base, FundingRate baseFundingRate, String quote, FundingRate quoteFundingRate, Long time) {
			super();
			put(base.toLowerCase(), baseFundingRate);
			put(quote.toLowerCase(), quoteFundingRate);
			put("timestamp", time);
		}
	}

	@Data
	@AllArgsConstructor
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class FundingRate {

		private BigDecimal lend;

		private BigDecimal borrow;
	}

	@Data
	@ApiModel(value = "SymbolFundingRateDemo")
	public static class SymbolFundingRateDemo {

		@ApiModelProperty(value = "base费率", example = "0.0003")
		private FundingRate btc;

		@ApiModelProperty(value = "quote费率", example = "0.0001")
		private FundingRate usd;

		@ApiModelProperty(value = "时间", example = "1634194831724")
		private Long timestamp;

		public static void main(String[] args) {
			System.err.println(SettlementsInfoRes.AvailableBalanceDemo.class.getName());
		}
	}

}
