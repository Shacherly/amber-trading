package com.google.backend.trading.model.common.model.aceup.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author trading
 * @date 2022/1/7 17:10
 */
@Data
@ApiModel(value = "aceup修改持仓限额请求数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionLimitUpdateReq {

	@NotBlank
	@ApiModelProperty(value = "用户id", required = true, example = "616289d4d4b1a6d195d6f288")
	private String uid;

	@ApiModelProperty(value = "杠杆持仓限额", required = true, example = "1000.2")
	private BigDecimal positionLimitAmount;

	@ApiModelProperty(value = "备注", example = "这是备注")
	private String remark;
}
