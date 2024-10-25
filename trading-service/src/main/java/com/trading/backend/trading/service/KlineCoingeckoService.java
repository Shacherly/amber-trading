package com.google.backend.trading.service;

import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/2/17 16:32
 */
public interface KlineCoingeckoService {

    /**
     * 获取指定币种kline价格，单位usd
     *
     * @param key 指定币种名称
     * @return BigDecimal 当前当前币种价格，单位usd
     */
    public BigDecimal getPrice(String key);
}
