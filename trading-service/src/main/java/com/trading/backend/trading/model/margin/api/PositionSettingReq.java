package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 21:05
 */
@Data
@ApiModel(value = "仓位设置请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionSettingReq {

	@NotNull(message = "position_id must be not null")
	@ApiModelProperty(name="position_id", value = "仓位id", example = "217a385c4ec3")
	private String positionId;

	@ApiModelProperty(name="auto_settle", value = "自动交割", example = "true")
	private Boolean autoSettle;

	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(name = "sl_price", value = "止损价格", example = "50000")
	private BigDecimal slPrice;

	@DecimalMin(value = "0", inclusive = false)
	@DecimalMax(value = "1")
	@ApiModelProperty(name="sl_percentage", value = "止损百分比", example = "0.67")
	private BigDecimal slPercentage;

	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(name = "tp_price", value = "止盈价格", example = "50000")
	private BigDecimal tpPrice;

	@DecimalMin(value = "0", inclusive = false)
	@DecimalMax(value = "1")
	@ApiModelProperty(name="tp_percentage", value = "止盈百分比", example = "0.45")
	private BigDecimal tpPercentage;

}
