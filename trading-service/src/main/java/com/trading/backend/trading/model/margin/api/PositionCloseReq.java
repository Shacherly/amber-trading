package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/16 17:28
 */
@Data
@ApiModel(value = "平仓请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseReq {
	@NotBlank
	@ApiModelProperty(value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
            example = "MARKET")
	private String type;

	@NotBlank
	@ApiModelProperty(value = "仓位id", example = "12341341234")
	private String positionId;

	@NotNull
	@PositiveOrZero
	@ApiModelProperty(value = "委托数量", required = true, example = "10")
	@Digits(integer = 32, fraction = 16)
	private BigDecimal quantity;

	@Positive
	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(value = "委托价格，for LIMIT单", example = "30000")
	private BigDecimal price;

	@Positive
	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(name = "trigger_price", value = "触发价", example = "20000")
	private BigDecimal triggerPrice;

	@Pattern(regexp = ">|<")
	@ApiModelProperty(name = "trigger_compare", value = "大于/小于触发价", allowableValues = ">,<", example = ">")
	private String triggerCompare;

	@ApiModelProperty(value = "备注", example = "梭哈抄底")
	private String notes;

	@ApiModelProperty(value = "执行策略, GTC, IOC, FOK", example = "GTC", hidden = true)
	private String strategy;

	@ApiModelProperty(value = "是否完全平仓", example = "true")
	private boolean all;

}
