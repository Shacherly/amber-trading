package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author trading
 * @date 2021/9/28 20:54
 */
@Data
@ApiModel(value = "杠杆下单响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderPlaceRes {

	@ApiModelProperty(value = "订单id", example = "uuid")
	private String orderId;

	@ApiModelProperty(value = "base币种成交数量", example = "10")
	private String baseFilled;

	@ApiModelProperty(value = "quote币种成交数量", example = "20")
	private String quoteFilled;

	@ApiModelProperty(value = "成交价格", example = "20000")
	private String filledPrice;

	@ApiModelProperty(value = "订单状态", example = "COMPLETE")
	private String status;
}
