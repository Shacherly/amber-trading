package com.google.backend.trading.model.open.v2.api;

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
@ApiModel(value = "v2-rfq响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RfqRes {

	@ApiModelProperty(value = "价格", example = "10000.5")
	private BigDecimal price;
}
