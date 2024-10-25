package com.google.backend.trading.model.spot.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 现货详情返回
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货详情返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpotDetailRes {
    @ApiModelProperty(value = "24小时涨跌", example = "0.23")
    private BigDecimal change24h = BigDecimal.ZERO;
    @ApiModelProperty(value = "30日涨跌", example = "-0.2324")
    private BigDecimal change30d  = BigDecimal.ZERO;
    @ApiModelProperty(value = "1年涨跌", example = "1.23")
    private BigDecimal change1y  = BigDecimal.ZERO;
    @ApiModelProperty(value = "价格", example = "23.02")
    private BigDecimal price = BigDecimal.ZERO;
    @ApiModelProperty(value = "买推荐价", example = "123.123")
    private BigDecimal buy = BigDecimal.ZERO;
    @ApiModelProperty(value = "卖推荐价", example = "32.323")
    private BigDecimal sell = BigDecimal.ZERO;
    @ApiModelProperty(value = "其他市场symbol", name = "symbols")
    private List<String> symbols;
    @ApiModelProperty(value = "是否收藏", example = "true")
    private Boolean favorite = false;
    @ApiModelProperty(value = "现货手续费率，计算公式：获得币种的数量（如涉及价格，获取ws的价格进行换算即可） * feeRate", example = "0.0005")
    private BigDecimal feeRate;

}
