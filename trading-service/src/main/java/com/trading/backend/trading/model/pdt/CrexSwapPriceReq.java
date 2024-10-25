package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author adam.wang
 * @date 2021/10/5 11:31
 */
@Data
@ApiModel(value = "swap询价请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CrexSwapPriceReq {
    private String from;
    private String to;
    private BigDecimal quantity;
    private BigDecimal toQuantity;
    private String queryId = UUID.randomUUID().toString();
}
