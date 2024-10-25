package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author trading
 * @date 2021/9/28 21:03
 */
@Data
@ApiModel(value = "历史仓位资金费用列表响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HistoryPositionFundingCostRes {

	@ApiModelProperty(value = "资金费用列表")
	private List<PositionFundingCostVo> list;

}
