package com.google.backend.trading.model.common.model.riskcontrol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/9 16:41
 */
@ApiModel(value = "平负余额请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LiquidBalanceReq {
    /**
     * 用户id
     */
    @NotNull(message = "uid must not be null")
    @ApiModelProperty(name="uid",value = "用户id", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String uid;


    @NotNull(message = "symbol must not be null")
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

    @NotNull(message = "direction must not be null")
    @ApiModelProperty(value = "方向，BUY SELL", required = true, notes="1:BUY 2:SELL", example = "BUY")
    private String direction;

    @NotNull(message = "is_quote must not be null")
    @ApiModelProperty(name="is_quote", value = "是否按照quote数量成交", required = true, example = "true")
    private Boolean isQuote;

    @NotNull(message = "quantity must not be null")
    @ApiModelProperty(value = "委托数量", required = true,
            notes="isQuote为真按quote数量，否则base数量", example = "1231")
    private BigDecimal quantity;


    @ApiModelProperty(value = "备注", example = "true")
    private String notes;

    @NotNull(message = "source must not be null")
    @ApiModelProperty(value = "请求来源,PLACED_BY_CLIENT、PLACED_BY_API、LIQUIDATION、CONVERSION", required = true,
            notes="1:用户手工单 2:用户程序单 3:系统强平 4:系统转换", example = "PLACED_BY_API")
    private String source;

}
