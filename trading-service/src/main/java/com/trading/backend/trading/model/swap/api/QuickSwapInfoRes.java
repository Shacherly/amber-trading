package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author adam.wang
 * @date 2021/9/28 14:52
 */
@ApiModel(value = "快捷兑换信息响应数据")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuickSwapInfoRes {

    @ApiModelProperty(value = "可用余额列表")
    private Map<String, BigDecimal> availableMap;

    @ApiModelProperty(value = "最小可购")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "最大可购")
    private BigDecimal maxAmount;
}
