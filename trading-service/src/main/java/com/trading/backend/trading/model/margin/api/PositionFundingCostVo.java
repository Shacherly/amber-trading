package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author trading
 * @date 2021/9/28 21:49
 */
@Data
@ApiModel(value = "仓位资金费率vo")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionFundingCostVo {

	@ApiModelProperty(value = "结算金额", example = "20000")
	private BigDecimal amount;

	@ApiModelProperty(value = "仓位数量", example = "100")
	private BigDecimal quantity;

	@ApiModelProperty(value = "结算价格", example = "50010")
	private BigDecimal price = new BigDecimal("50010");

	@ApiModelProperty(value = "时间", example = "1632835745000")
	private Date time;
}
