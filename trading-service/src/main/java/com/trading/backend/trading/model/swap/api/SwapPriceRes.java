package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/28 14:42
 */
@ApiModel(value = "兑换报价返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapPriceRes {

    @ApiModelProperty(value = "正向价格", notes="base/quote的价格",
            required = true, example = "2.34")
    private BigDecimal price;

    @ApiModelProperty(value = "反向价格", required = true, example = "0.5")
    private BigDecimal reversePrice;

    @ApiModelProperty(value = "报价对应的symbol，from coin + '_' + to coin", example = "BTC_USD")
    private String symbol;

}
