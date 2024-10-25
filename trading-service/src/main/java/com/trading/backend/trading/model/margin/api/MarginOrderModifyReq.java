package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 20:45
 */
@Data
@ApiModel(value = "杠杆订单修改请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderModifyReq {
	@NotNull
	@ApiModelProperty(name="order_id", value = "订单id", required = true, example = "21312")
	private String orderId;

	@NotNull
	@ApiModelProperty(name="last_status",
			value = "变更前的订单状态, PRE_TRIGGER 待触发，EXECUTING 执行中，EXCEPTION 异常",
			example = "EXECUTING")
	private String lastStatus;

	@NotNull
	@ApiModelProperty(name = "last_quantity", value = "变更前的委托数量", example = "21312")
	@Max(value = Long.MAX_VALUE)
	private BigDecimal lastQuantity;

	@ApiModelProperty(name = "last_price", value = "变更前的委托价格, 限价单传", example = "21312")
	@Max(value = Long.MAX_VALUE)
	private BigDecimal lastPrice;

	@ApiModelProperty(name = "last_trigger_price", value = "变更前的触发价，条件单传", example = "21312")
	@Max(value = Long.MAX_VALUE)
	private BigDecimal lastTriggerPrice;

	@ApiModelProperty(name = "last_trigger_compare", value = "变更前的触发类型，条件单传", example = "21312")
	private String lastTriggerCompare;

	@ApiModelProperty(name = "last_filled_quantity", value = "变更前的已成交数量", example = "21312")
	@Max(value = Long.MAX_VALUE)
	@NotNull
	private BigDecimal lastFilledQuantity;

	@ApiModelProperty(name="quantity", value = "委托数量", example = "21312")
	@Digits(integer = 32, fraction = 16)
	@Positive
	private BigDecimal quantity;

	@Positive
	@Max(value = Long.MAX_VALUE)
	@ApiModelProperty(name = "price", value = "委托价格", example = "21312")
	private BigDecimal price;

	@ApiModelProperty(name = "trigger_price", value = "触发价格", example = "21312")
	@Max(value = Long.MAX_VALUE)
	@Positive
	private BigDecimal triggerPrice;

	@ApiModelProperty(name="trigger_compare", value = "大于/小于触发价", allowableValues = ">,<", example = ">")
	private String triggerCompare;

	@ApiModelProperty(value = "备注", example = "梭哈抄底")
	private String notes;
}
