package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.crex.CrexTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 去PDT下单的请求数据
 *
 * @author savion.chen
 * @date 2021/10/4 15:37
 */
@Data
@ApiModel(value = "下单的请求体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateTradeReq {

    private String userId;

    private String tradeId;
    private String orderId;

    private String direction;
    private String symbol;

    private BigDecimal quantity;
    private BigDecimal quantityLimit;

    private BigDecimal quoteQuantity;
    private BigDecimal quoteQuantityLimit;

    private BigDecimal price;
    private CrexTypeEnum type;
    private List<String> notes;
}

