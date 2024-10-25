package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author adam.wang
 * @date 2021/10/1 18:13
 */
@Data
@ApiModel(value = "杠杆订单详情请求对象")
@RequestUnderlineToCamel
public class MarginOrderDetailReq {

    @NotBlank
    @ApiModelProperty(name="order_id",value = "订单id", example = "3213213")
    private String orderId;
}
