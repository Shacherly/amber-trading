package com.google.backend.trading.model.common.model.bff;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/9/28 17:42
 */
@Data
@ApiModel(value = "bff杠杆信息响应数据")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginInfoVo {

	@ApiModelProperty(value = "浮动盈亏", example = "100")
	private BigDecimal unpnl = BigDecimal.ZERO;

	@ApiModelProperty(value = "风险率", example = "0.50")
	private BigDecimal riskRate = BigDecimal.ZERO;

}
