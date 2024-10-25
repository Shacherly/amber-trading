package com.google.backend.trading.model.trade;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

/**
 * kafka algo算法模块 交易额
 *
 * @author david.chen
 * @date 2022/1/7 11:17
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AlgoTradeAmountDTO {
    private String uid;
    private String coin;
    private BigDecimal amount;
    private String tradeId;
}
