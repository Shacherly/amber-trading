package com.google.backend.trading.model.booking.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author trading
 * @date 2021/11/4 15:01
 */
@Data
@ApiModel(value = "booking可用余额响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookingAvailableRes {

	@ApiModelProperty(value = "信用额度", example = "30000")
	private BigDecimal credit;

	@ApiModelProperty(value = "占用信用额度", example = "20000")
	private BigDecimal usedCredit;

	@ApiModelProperty(value = "余额", example = "2.3")
	private Map<String, BigDecimal> balance;

	@ApiModelProperty(value = "可用保证金", example = "50000.123")
	private BigDecimal availableMargin;

	@ApiModelProperty(value = "总保证金", example = "50000.123")
	private BigDecimal totalMargin;
}
