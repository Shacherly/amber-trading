package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel(value = "在持仓位信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivePositionInfoVo {

	@ApiModelProperty(value = "仓位ID", example = "423fd4545")
	private String positionId;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "10")
	private BigDecimal quantity;

	@ApiModelProperty(value = "持仓头寸", example = "10")
	private BigDecimal position;

	@ApiModelProperty(value = "占用保证金", example = "50000")
	private BigDecimal usedMargin;

	@ApiModelProperty(value = "实现盈亏", example = "-56")
	private BigDecimal pnl;

	@ApiModelProperty(value = "开仓价格", example = "5000")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "浮动盈亏", example = "100.21")
	private BigDecimal unpnl;

	@JsonIgnore
	private BigDecimal originalUnpnl;

	@ApiModelProperty(value = "浮动盈亏收益率", example = "0.30")
	private BigDecimal unpnlRate;

	@ApiModelProperty(value = "预计资金费用", example = "5.123")
	private BigDecimal expectedFundingCost;

	@ApiModelProperty(value = "是否自动交割", example = "true")
	private Boolean autoSettle;

	@ApiModelProperty(value = "更新时间", example = "1634194831724")
	private Long mtime;
	@ApiModelProperty(value = "创建时间", example = "1634194831724")
	private Long ctime;

	@ApiModelProperty(value = "预计盈亏单位", example = "USD")
	private String unpnlCoin;

	@ApiModelProperty(value = "止损价格", example = "5.123")
	private BigDecimal stopLossPrice;

	@ApiModelProperty(value = "止损占仓位比，默认0，表示不开启止损", example = "0.123")
	private BigDecimal stopLossPercentage;

	@ApiModelProperty(value = "止盈价格", example = "5.123")
	private BigDecimal takeProfitPrice;

	@ApiModelProperty(value = "止盈占仓位比，默认0，表示不开启止盈", example = "0.123")
	private BigDecimal takeProfitPercentage;

	@ApiModelProperty(value = "资金费率", example = "0.123")
	private BigDecimal fundingCostRate;

	@ApiModelProperty(value = "币对资金费率")
	private SymbolFundingRate symbolFundingRate;

	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class SymbolFundingRate extends HashMap<String, Object> {

		public SymbolFundingRate(String base, FundingRate baseFundingRate, String quote, FundingRate quoteFundingRate, Long time) {
			super();
			put(base, baseFundingRate);
			put(quote, quoteFundingRate);
			put("time",time);
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
