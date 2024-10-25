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
@ApiModel(value = "仓位资金费率列表响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionFundingCostRes {

	@ApiModelProperty(value = "仓位资金费率")
	private List<PositionFundingCostVo> list;

}
