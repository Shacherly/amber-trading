package com.google.backend.trading.model.open.v2.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/15 19:35
 */
@Data
@ApiModel(value = "v2-openapi交割请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SettlementsReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(value = "仓位id", required = true, example = "uuid")
	private String positionId;

	@NotNull
	@Positive
	@ApiModelProperty(value = "交割数量", required = true, example = "1000")
	@Digits(integer = 32, fraction = 16)
	private BigDecimal quantity;
}
