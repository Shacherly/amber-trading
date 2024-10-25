package com.google.backend.trading.model.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/10/16 19:22
 */
@ApiModel(value = "修改记录杠杆OR现货")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderModificationVo {

    @ApiModelProperty(name="order_id",value = "订单id", example = "123123")
    private String orderId;

    @ApiModelProperty(value = "委托数量", required = true,
            notes="base数量成交即base数量，quote数量成交即quote数量", example = "1231")
    private BigDecimal quantity;

    @ApiModelProperty(name="lock_amount",value = "锁定数额", example = "1231")
    private BigDecimal lockAmount;

    @ApiModelProperty(value = "委托价格", notes="limit单传入", example = "1231.2")
    private BigDecimal price;

    @ApiModelProperty(value = "触发价格", example = "4563")
    private BigDecimal triggerPrice;

    @ApiModelProperty(value = "成交价格", example = "4321")
    private BigDecimal filledPrice;

    @ApiModelProperty(name = "current_status", value = "订单状态 PRE_TRIGGER 待触发, EXECUTING 挂单中, EXCEPTION 异常, CANCELED 已取消, COMPLETED 完全成交",
			notes = "1 待触发，2 执行中，3 已完成，4 取消", example = "CANCELED")
    private String currentStatus;


    @ApiModelProperty(value = "创建时间", notes="13位毫秒时间戳" ,example = "1633425311991")
    private Date ctime;

    @ApiModelProperty(value = "备注", example = "梭哈抄底")
    private String notes;

}
