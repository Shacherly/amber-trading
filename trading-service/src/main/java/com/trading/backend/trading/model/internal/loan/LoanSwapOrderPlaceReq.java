package com.google.backend.trading.model.internal.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.user.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/9/28 14:42
 */
@ApiModel(value = "借贷兑换下单请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSwapOrderPlaceReq {
    @NotBlank(message = "orderId不能为空")
    @ApiModelProperty(name = "order_id", required = true, example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true, example = "33345")
    private String uid;

    @NotBlank(message = "支付币种不能为空")
    @ApiModelProperty(name = "from_coin", value = "支付币种", required = true, example = "BTC")
    private String fromCoin;

    @NotBlank(message = "获得币种不能为空")
    @ApiModelProperty(name = "to_coin", value = "获得币种", required = true, example = "USDT")
    private String toCoin;

    @ApiModelProperty(name = "price", value = "下单汇率，为空时，交易模块会重新查询价格，并按当前价下单", example = "1231.2")
    @Positive
    private BigDecimal price;

    @NotBlank(message = "委托模式不能为空")
    @ApiModelProperty(name = "mode", allowableValues = "PAYMENT,OBTAINED", required = true, example = "PAYMENT")
    private String mode;

    @NotNull(message = "委托数量不能为空")
    @ApiModelProperty(value = "委托数量", required = true, example = "123")
    @Positive
    private BigDecimal quantity;

    @NotBlank(message = "来源不能为空")
    @ApiModelProperty(value = "来源", allowableValues = "LOAN_LIQUIDATION,REPAY_WITH_COLLATERAL", required = true, example = "REPAY_WITH_COLLATERAL")
    private String source;

}
