package com.google.backend.trading.model.kline.coingecko.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

/**
 * kline价格接口结果类
 *
 * @author david.chen
 * @date 2022/2/17 16:29
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PriceModel {
    private String token;
    private BigDecimal price;
    private long timestamp;
    private String quote;
}
