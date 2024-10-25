package com.google.backend.trading.model.common.model.aceup.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author trading
 * @date 2022/1/7 17:30
 */
@Data
@ApiModel(value = "aceup-liquid名单查询数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LiquidListRes {

	@ApiModelProperty(value = "用户id", example = "616289d4d4b1a6d195d6f288")
	private String uid;

	@ApiModelProperty(value = "13位毫秒时间戳", example = "1641547944000")
	private Date ctime;

	@ApiModelProperty(value = "备注", example = "这是备注")
	private String remark;
}
