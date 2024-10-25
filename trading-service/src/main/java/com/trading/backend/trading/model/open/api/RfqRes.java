package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/15 14:24
 */
@Data
@ApiModel(value = "rfq响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RfqRes {

	@ApiModelProperty(value = "占用保证金，单位USD", example = "10000.5")
	private String rfqId;

	@ApiModelProperty(value = "占用保证金，单位USD", example = "10000.5")
	private Long validUntil;

	@ApiModelProperty(value = "占用保证金，单位USD", example = "10000.5")
	private BigDecimal price;
}
