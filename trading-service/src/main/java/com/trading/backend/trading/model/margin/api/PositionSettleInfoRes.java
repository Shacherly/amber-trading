package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 22:11
 */
@Data
@ApiModel(value = "仓位可交割信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionSettleInfoRes {

	@ApiModelProperty(value = "仓位ID", example = "423fd4545")
	private String positionId;

	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

	@ApiModelProperty(value = "持仓数量", example = "10")
	private BigDecimal positionQuantity;

	@ApiModelProperty(value = "开仓价格", example = "5000")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "最大交割数量", example = "5000")
	private BigDecimal maxSettleQuantity;

	@ApiModelProperty(value = "币种的余额", example = "5000")
	private BigDecimal availableBalance;

	@ApiModelProperty(value = "交割手续费率，计算公式：获得币种的数量（如涉及价格，获取持仓均价进行换算） * feeRate", example = "0.0005")
	private BigDecimal marginSettleFeeRate;
}
