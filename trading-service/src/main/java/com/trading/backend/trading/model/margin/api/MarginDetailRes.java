package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 17:42
 */
@Data
@ApiModel(value = "杠杆详情响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginDetailRes {

	@ApiModelProperty(value = "base最大开仓数量", example = "200000")
	private BigDecimal maxOpen;

	@ApiModelProperty(value = "当前币对资金费率", example = "0.001")
	private BigDecimal fundingRate;

	@ApiModelProperty(value = "是否收藏", example = "true")
	private boolean favorite;

	@ApiModelProperty(value = "杠杆手续费率, 计算公式：base数量 * base币种对USD的价格 * feeRate", example = "0.0005")
	private BigDecimal feeRate = BigDecimal.ZERO;
}
