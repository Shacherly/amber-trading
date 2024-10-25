package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 21:03
 */
@Data
@ApiModel(value = "历史仓位列表响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HistoryPositionRes {

	@ApiModelProperty(value = "统计盈亏（筛选后的列表持仓pnl累加）", example = "111")
	private BigDecimal totalPnl;

	@ApiModelProperty(value = "统计盈亏的天数", example = "30")
	private Integer days;

	@ApiModelProperty(value = "历史仓位列表")
	private PageResult<HistoryPositionInfoVo> data;

}
