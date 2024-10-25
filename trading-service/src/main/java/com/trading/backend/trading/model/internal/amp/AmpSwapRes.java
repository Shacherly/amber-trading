package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "APM兑换返回对象")
public class AmpSwapRes {

    /**
     * 用户id
     */
    @NotBlank
    @ApiModelProperty(name = "uid",value = "用户编码", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String uid;

    @ApiModelProperty(name = "uuid",value = "兑换ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String uuid;

    @ApiModelProperty(value = "委托模式: 委托支付 PAYMENT,委托获得 OBTAINED", example = "OBTAINED")
    private String mode;

    @ApiModelProperty(name="from_coin",value = "支付币种",  example = "BTC")
    private String fromCoin;

    @ApiModelProperty(name="to_coin",value = "获得币种",  example = "USD")
    private String toCoin;

    @ApiModelProperty(name="status",value = "订单状态 PENDING 待触发 EXECUTING 挂单中, CANCELED 已取消, COMPLETED 完全成交 LOCKED 锁定中 ",
            example = "CANCELED")
    private String status;

    @ApiModelProperty(value = "手续费E", example = "2.2")
    private BigDecimal fee;

    @ApiModelProperty(name="fee_rate",value = "费率", example = "2.2")
    private BigDecimal feeRate;


    @ApiModelProperty(name="order_price",value = "下单时的价格（支付币种/获得币种）", example = "2.2")
    private BigDecimal orderPrice;

    @ApiModelProperty(name="deal_price",value = "成交价格（支付币种/获得币种） 可能为null", example = "2.2")
    private BigDecimal dealPrice;

    @ApiModelProperty(value = "备注 可能为null", example = "2.2")
    private String memo;

    @ApiModelProperty(value = "创建时间", example = "")
    private Date ctime;

    @ApiModelProperty(value = "修改时间", example = "")
    private Date mtime;


     @ApiModelProperty(name="from_quantity",value = "支付数量", example = "2.2")
     private BigDecimal fromQuantity;


    @ApiModelProperty(name="to_quantity",value = "获得数量", example = "2.2")
    private BigDecimal toQuantity;


    @ApiModelProperty(value = "PLACED_BY_CLIENT（用户下的手工单） PLACED_BY_API（用户通过API下的程序化订单） LIQUIDATION（清算订单） AUTO_CONVERSION（盈亏转换） OTC_SHOP(BOOKING用户来源)\n" +
            "  FORCE_CLOSE（强平或强制减仓）LOAN_LIQUIDATION（借贷强平订单）EARN_LIQUIDATION（理财清算）REPAY_WITH_COLLATERAL（借贷质押还款订单）AUTO_POSITION_SETTLE（自动交割）\n" +
            "  TAKE_PROFIT_STOP_LOSS（止盈止损）", example = "PLACED_BY_CLIENT")
    private String source;

    @ApiModelProperty(name="fee_coin",value = " 手续费币种 可能为null", example = "USD")
    private String feeCoin;
}
