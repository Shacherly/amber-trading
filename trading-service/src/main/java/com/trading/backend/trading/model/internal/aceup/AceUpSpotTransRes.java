package com.google.backend.trading.model.internal.aceup;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2022/3/17 17:28
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "aceup-现货交易记录返回对象")
public class AceUpSpotTransRes {

    @ApiModelProperty(name = "trans_id", value = "成交ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String transId;

    @ApiModelProperty(value = "状态：FAILED（失败）、COMPLETED（成功）", example = "COMPLETED")
    private String status;

    @ApiModelProperty(value = "成交均价", example = "1.6")
    private BigDecimal price;

    @ApiModelProperty(name = "base_quantity", value = "成交量 quote", example = "1.6")
    private BigDecimal baseQuantity;

    @ApiModelProperty(name = "quote_quantity", value = "成交额度 base", example = "1.6")
    private BigDecimal quoteQuantity;

    @ApiModelProperty(value = "手续费", example = "1.6")
    private BigDecimal fee;

    @ApiModelProperty(name = "fee_coin", value = "手续费币种", example = "1.6")
    private String feeCoin;

    @ApiModelProperty(value = "成交时间", example = "1.6")
    private Date mtime;

}
