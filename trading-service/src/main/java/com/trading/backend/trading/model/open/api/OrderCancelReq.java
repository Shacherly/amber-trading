package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author trading
 * @date 2021/9/28 20:45
 */
@Data
@ApiModel(value = "openapi取消订单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderCancelReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(required = true, example = "6c61cf0b-bbf4-4bd0-afc9-f46b12ff0542")
	private String orderId;

}
