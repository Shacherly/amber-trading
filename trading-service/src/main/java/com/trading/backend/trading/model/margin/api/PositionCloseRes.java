package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "杠杆下单请求返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionCloseRes {
    @ApiModelProperty(value = "订单id", example = "21312")
    private String orderId;
}
