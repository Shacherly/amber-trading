package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/15 14:07
 */
@Data
@ApiModel(value = "openapi杠杆信息响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginInfoRes {

	@ApiModelProperty(value = "占用保证金，单位USD", example = "10000.5")
	private BigDecimal usedMargin;

	@ApiModelProperty(value = "未实现盈亏，单位USD", example = "100.3")
	private BigDecimal unpnl;

	@ApiModelProperty(value = "占用信用额度，单位USD", example = "11000.5")
	private BigDecimal usedCredit;

	@ApiModelProperty(value = "排除信用额度外的占用保证金，单位USD", example = "1000")
	private BigDecimal usedMarginWithoutCredit;


}
