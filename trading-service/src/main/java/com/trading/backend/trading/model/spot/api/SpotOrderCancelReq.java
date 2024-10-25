package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 现货撤单接口
 *
 * @author savion.chen
 * @date 2021/10/14 10:51
 */
@ApiModel(value = "现货撤单请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotOrderCancelReq {

    @NotBlank
    @ApiModelProperty(name="order_id", value = "订单id", required = true, example = "21312")
    private String orderId;
}
