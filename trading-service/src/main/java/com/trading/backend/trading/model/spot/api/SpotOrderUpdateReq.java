package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货修改请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotOrderUpdateReq {

    @NotNull
    @ApiModelProperty(name="order_id", value = "订单id", required = true, example = "21312")
    private String orderId;

    @Max(value = Long.MAX_VALUE)
    @ApiModelProperty(name = "last_quantity", value = "变更前的委托数量", example = "21312")
    private BigDecimal lastQuantity;

    @ApiModelProperty(name="last_status",
            value = "变更前的订单状态,PRE_TRIGGER 待触发，EXECUTING 执行中，COMPLETED 已完成，CANCELED 取消",
            example = "EXECUTING")
    private String lastStatus;

    @ApiModelProperty(name="quantity", value = "委托数量", example = "21312")
    @Digits(integer = 32, fraction = 16)
    @Positive
    private BigDecimal quantity;

    @ApiModelProperty(name="price", value = "委托价格", example = "21312")
    @Positive
    @Max(value = Long.MAX_VALUE)
    private BigDecimal price;

    @ApiModelProperty(name="trigger_price", value = "触发价格", example = "21312")
    @Positive
    @Max(value = Long.MAX_VALUE)
    private BigDecimal triggerPrice;

    @ApiModelProperty(value = "大于/小于触发价", allowableValues = ">,<", example = ">")
    private String triggerCompare;

    @ApiModelProperty(value = "备注", example = "梭哈抄底")
    private String notes;
}
