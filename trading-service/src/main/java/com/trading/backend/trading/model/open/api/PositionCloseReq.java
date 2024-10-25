package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.config.web.StringUpperCaseDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author trading
 * @date 2021/10/23 21:03
 */
@Data
@ApiModel(value = "openapi平仓请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseReq {

	@NotBlank
	@ApiModelProperty(required = true, example = "61839e37361f90c868238228")
	private String uid;

	@NotBlank
	@ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
	@JsonProperty(value = "contract")
	@JsonDeserialize(using = StringUpperCaseDeserializer.class)
	private String symbol;
}
