package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/5 11:36
 */
@Data
@ApiModel(value = "swap下单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateSwapReq {
    private String tradeId;
    private String from;
    private String to;
    private BigDecimal quantity;
    private BigDecimal toQuantity;
    private BigDecimal price;
}
