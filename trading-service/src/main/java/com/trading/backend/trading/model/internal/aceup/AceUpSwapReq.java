package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/9/28 14:52
 */
@ApiModel(value = "aceup-swapOrder查询请求")
@Data
@RequestUnderlineToCamel
public class AceUpSwapReq extends PageTimeRangeExtReq {
    @ApiModelProperty(name = "order_id", value = "订单id", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @ApiModelProperty(value = "用户id", example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String uid;

    @ApiModelProperty(name = "from_coin_list", value = "支付币种", example = "[\"BTC\",\"ETH\"]")
    private List<String> fromCoinList;

    @ApiModelProperty(name = "to_coin_list", value = "获得币种", example = "[\"USD\",\"USDT\"]")
    private List<String> toCoinList;

    @ApiModelProperty(value = "兑换方式 PAYMENT: 委托支付（固定支付）  OBTAINED：委托获得（固定获得） ", example = "123213")
    @Pattern(regexp = "PAYMENT|OBTAINED", message = "委托模式不正确")
    private String mode;

    @ApiModelProperty(name = "status_list", value = "订单状态: EXECUTING, COMPLETED, CANCELED",
            notes = "EXECUTING 执行中 COMPLETED 已完成 CANCELED 已取消", example = "[\"EXECUTING\", \"COMPLETED\"]")
    private List<String> statusList;

    @ApiModelProperty(value = "订单来源 PLACED_BY_CLIENT(用户下单) REPAY_WITH_COLLATERAL(借贷质押还款订单)AUTOMATIC_INVESTMENT_PLAN（定投）", example = "AUTOMATIC_INVESTMENT_PLAN")
    private String source;
}
