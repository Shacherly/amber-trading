package com.google.backend.trading.model.internal.amp;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "APM兑换查询对象")
public class AmpSwapReq extends PageTimeRangeExtReq {

    /**
     * 用户id
     */
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(name = "uid",value = "用户编码", required = true, example = "61628983d4b1a6d195d6f285")
    private String uid;

    @ApiModelProperty(name = "uuid",value = "兑换ID", example = "d05e1e7e-5cda-49e0-92dd-9f6dfe035e06")
    private String uuid;

    @ApiModelProperty(value = "委托模式: 委托支付 PAYMENT,委托获得 OBTAINED", example = "OBTAINED")
    private String mode;

    @ApiModelProperty(name="from_coin",value = "支付币种", example = "BTC")
    private String fromCoin;

    @ApiModelProperty(name="to_coin",value = "获得币种",example = "USD")
    private String toCoin;

    @ApiModelProperty(name="status",value = "订单状态 PENDING 待处理 EXECUTING 挂单中, CANCELED 已取消, COMPLETED 完全成交 LOCKED 锁定中 ",
            example = "CANCELED")
    private String status;


}
