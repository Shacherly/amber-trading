package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询Swap交易返回
 * @author adam.wang
 * @date 2021/10/5 11:36
 */
@Data
@ApiModel(value = "查询指定订单响应")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class SwapByIdRes {
    private String tradeId;
    private BigDecimal price;
    private String from;
    private String to;

    private String status;
    private BigDecimal filled;
    private BigDecimal toFilled;
    private BigDecimal filledPrice;
    private Date ctime;
}
