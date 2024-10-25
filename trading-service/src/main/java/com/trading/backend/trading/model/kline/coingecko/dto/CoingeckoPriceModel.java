package com.google.backend.trading.model.kline.coingecko.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

/**
 * kline查询单个币对接口结果类
 *
 * @author david.chen
 * @date 2022/2/17 16:26
 */

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoingeckoPriceModel {
    private BigDecimal price;
    private long timestamp;
    private BigDecimal usdMarketCap;
    @JsonProperty("usd_24h_vol")
    private BigDecimal usd24hVol;
}
