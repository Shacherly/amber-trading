package com.google.backend.trading.model.open.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.backend.trading.config.web.StringLowerCaseSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "openapi交割记录")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionSettleHistoryVo {
    @ApiModelProperty(value = "交易Id", example = "12312")
    @JsonProperty("settle_id")
    private String transactionId;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    @JsonProperty("contract")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String symbol;

    @ApiModelProperty(value = "finished", example = "finished")
    private final String status = "finished";

    @ApiModelProperty(value = "base币种", example = "BTC")
    @JsonProperty("base_asset")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String base;

    @ApiModelProperty(value = "base数量", example = "50")
    @JsonProperty("base_amount")
    private BigDecimal baseQuantity;

    @ApiModelProperty(value = "quote币种", example = "USD")
    @JsonProperty("quote_asset")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String quote;

    @ApiModelProperty(value = "quote数量", example = "50")
    @JsonProperty("quote_amount")
    private BigDecimal quoteQuantity;

    @ApiModelProperty(value = "时间", notes = "13位毫秒时间戳", example = "1632822186544")
    @JsonProperty("created_time")
    private Long ctime;

    @ApiModelProperty(value = "时间，兼容", notes = "13位毫秒时间戳", example = "1632822186544")
    @JsonProperty("last_updated_time")
    private Long mtime;

    @ApiModelProperty(value = "方向,buy 买，sell", notes="buy 买，sell卖", example = "buy")
    @JsonSerialize(using = StringLowerCaseSerializer.class)
    private String direction;

}
