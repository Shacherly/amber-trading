package com.google.backend.trading.model.internal.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/10/08
 */
@ApiModel(value = "借贷现货下单请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSpotOrderPlaceReq {
    @NotBlank(message = "orderId不能为空")
    @ApiModelProperty(name = "order_id", required = true, example = "1d4a727e-bf6b-4afc-ad22-3a10e703359d")
    private String orderId;

    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true, example = "33345")
    private String uid;

    @NotBlank(message = "币对不能为空")
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USDT")
    private String symbol;

    @NotBlank(message = "方向不能为空")
    @ApiModelProperty(value = "方向", required = true, allowableValues = "BUY,SELL", example = "BUY")
    private String direction;

    @NotNull(message = "是否按照quote数量成交")
    @ApiModelProperty(name = "is_quote", value = "是否按照quote数量成交", example = "true")
    private Boolean isQuote;

    @NotNull(message = "委托数量不能为空")
    @ApiModelProperty(value = "委托数量", required = true,
            notes = "isQuote为真按quote数量，否则base数量", example = "1231")
    @Positive
    private BigDecimal quantity;

    @NotBlank(message = "来源不能为空")
    @ApiModelProperty(value = "来源", allowableValues = "LOAN_LIQUIDATION,REPAY_WITH_COLLATERAL", required = true, example = "REPAY_WITH_COLLATERAL")
    private String source;
}
