package com.google.backend.trading.model.aceup.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author trading
 * @date 2022/1/7 17:10
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequestUnderlineToCamel
@ApiModel(value = "aceup新增liquid名单请求数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LiquidListFindReq extends PageTimeRangeReq {

	@ApiModelProperty(value = "用户id", example = "616289d4d4b1a6d195d6f288")
	private String uid;
}
