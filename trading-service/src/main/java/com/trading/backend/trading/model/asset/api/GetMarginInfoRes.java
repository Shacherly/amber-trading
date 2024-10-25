package com.google.backend.trading.model.asset.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2022/2/16 15:59
 */
@Data
@AllArgsConstructor
@ApiModel(value = "保证金信息for资金响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetMarginInfoRes {

	@ApiModelProperty(value = "已占用保证金", example = "20000")
	private BigDecimal usedMargin;

}
