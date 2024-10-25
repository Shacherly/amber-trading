package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageReq;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "APM交易查询对象")
public class AmpTransReq extends PageReq {

    /**
     * 用户id
     */
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(name = "uid",value = "用户id", required = true, example = "615309c065a76ea30fd8a156")
    private String uid;
    @NotBlank(message = "order_id 不能为空")
    @ApiModelProperty(name = "order_id",value = "订单id",required = true, example = "03fa9c8f-c64d-4418-a877-2b3032c25a1d")
    private String orderId;



}
