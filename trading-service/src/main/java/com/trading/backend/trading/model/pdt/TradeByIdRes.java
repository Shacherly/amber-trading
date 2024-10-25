package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 查询交易返回
 * @author adam.wang
 * @date 2021/10/5 11:36
 */
@Data
@ApiModel(value = "查询现货订单数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeByIdRes {
    private String tradeId;
    private String orderId;
    private String direction;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal quantityLimit;
    private BigDecimal quoteQuantity;
    private BigDecimal quoteQuantityLimit;
    private BigDecimal price;
    private String type;
    private List<String> notes;
    private Date ctime;
    private String status;
    private BigDecimal filled;
    private BigDecimal quoteFilled;
    private BigDecimal filledPrice;
}
