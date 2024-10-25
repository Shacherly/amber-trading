package com.google.backend.trading.model.user.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author trading
 * @date 2022/1/4 11:24
 */
@Data
@AllArgsConstructor
@ApiModel(value = "用户注销信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDeletionInfo {

	@ApiModelProperty(value = "是否存在margin订单或者仓位，true表示存在订单或者仓位", example = "false")
	private boolean existOrderOrPosition;

}
