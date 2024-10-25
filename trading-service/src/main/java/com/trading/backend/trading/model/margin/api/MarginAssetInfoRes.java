package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author trading
 * @date 2021/9/28 17:42
 */
@Data
@ApiModel(value = "杠杆资产信息")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginAssetInfoRes {

	@ApiModelProperty(value = "总保证金", example = "10000")
	private BigDecimal totalOpenMargin = BigDecimal.ZERO;

	@ApiModelProperty(value = "清算总保证金", example = "10000")
	private BigDecimal totalLiquidMargin = BigDecimal.ZERO;

	@ApiModelProperty(value = "占用保证金", example = "65000")
	private BigDecimal usedMargin = BigDecimal.ZERO;

	@ApiModelProperty(value = "资金使用率，占用保证金和总保证金的比重", example = "0.65")
	private BigDecimal fundUtilization = BigDecimal.ZERO;

	@ApiModelProperty(value = "浮动盈亏", example = "100")
	private BigDecimal unpnl = BigDecimal.ZERO;

	@ApiModelProperty(value = "持仓杠杆", example = "10000")
	private BigDecimal currentLeverage = BigDecimal.ZERO;

	@ApiModelProperty(value = "风险率", example = "0.50")
	private BigDecimal riskRate = BigDecimal.ZERO;

	@ApiModelProperty(value = "可开仓数量", example = "0.50")
	private BigDecimal canOpenUsd = BigDecimal.ZERO;

	@ApiModelProperty(value = "杠杆可用保证金", example = "0.50")
	private BigDecimal availableMargin = BigDecimal.ZERO;

	@ApiModelProperty(value = "当前活跃仓位,按头寸排序")
	private List<ActivePositionInfoVo> currentPositionVos;

}
