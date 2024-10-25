package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/5 11:50
 */
@Data
@ApiModel(value = "查询最新价响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PriceRes {
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal mid;
}
