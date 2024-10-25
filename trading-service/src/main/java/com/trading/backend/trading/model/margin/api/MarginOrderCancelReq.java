package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author adam.wang
 * @date 2021/10/1 18:13
 */
@Data
@ApiModel(value = "杠杆取消请求对象")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderCancelReq {
    @NotNull
    @NotBlank
    @ApiModelProperty(name="order_id",value = "订单id", example = "3213213")
    private String orderId;
}
