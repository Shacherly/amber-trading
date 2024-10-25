package com.google.backend.trading.model.eaas.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2022/2/16 16:07
 */
@Data
@AllArgsConstructor
@ApiModel(value = "交易信息for算法响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeInfoRes {

	@ApiModelProperty("交易等级")
	private Integer tradeLevel;

	@ApiModelProperty("30天交易额")
	private BigDecimal tradeAmount30d;

	@ApiModelProperty("交易抵扣率")
	private BigDecimal feeOff;
}
