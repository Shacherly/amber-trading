package com.google.backend.trading.model.swap.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 根据指定订单ID查询订单状态
 *
 * @author savion.chen
 * @date 2021/10/11 19:20
 */
@ApiModel(value = "查询指定订单")
@Data
@RequestUnderlineToCamel
public class SwapQueryReq {

    @ApiModelProperty(value = "订单ID", required = true, example = "12345")
    private String orderId;
}
