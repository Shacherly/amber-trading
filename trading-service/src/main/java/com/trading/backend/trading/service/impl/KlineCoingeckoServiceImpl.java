package com.google.backend.trading.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.KlineCoingeckoClinent;
import com.google.backend.trading.model.kline.coingecko.dto.CoingeckoPriceModel;
import com.google.backend.trading.model.kline.coingecko.dto.PriceModel;
import com.google.backend.trading.service.KlineCoingeckoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author david.chen
 * @date 2022/2/17 16:30
 */
@Slf4j
@Service
public class KlineCoingeckoServiceImpl implements KlineCoingeckoService {

    /**
     * Kline币种价格
     * 单位:usd
     */
    private static final Map<String, BigDecimal> KLINE_PRICE_MAP = new ConcurrentHashMap<>();
    private ScheduledExecutorService executorService;

    @Autowired
    private KlineCoingeckoClinent klineCoingeckoClinent;

    @PostConstruct
    private void init() {
        executorService = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder().setNameFormat("klineService-schedule-pool-%d").build());

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refreshKlinePrice();
            }
        }, 0, 180, TimeUnit.SECONDS);
    }

    /**
     * 批量刷新kline价格数据
     */
    private void refreshKlinePrice() {
        List<PriceModel> priceModelList = new ArrayList<>();
        try {
            Response<List<PriceModel>> response = klineCoingeckoClinent.getPriceBatch();
            log.info("klineClient.getPriceBatch：{}", response);
            if (response.getCode() != 0 || response.getData() == null) {
                log.error("[log---alarm]refreshKlinePrice failed, code = {}", response.getCode());
            }
            priceModelList = response.getData();
            for (PriceModel priceModel : priceModelList) {
                KLINE_PRICE_MAP.put(priceModel.getToken(), priceModel.getPrice());
            }
        } catch (Exception e) {
            log.error("[log---alarm]refreshKlinePrice Exception: {} ", e.getMessage());
        }
    }

    /**
     * 查询单个币种的kiline价格
     *
     * @param coin
     * @return
     */
    private void fetchCoinPrice(String coin) {
        try {
            Response<CoingeckoPriceModel> response = klineCoingeckoClinent.getCoinPrice(coin);
            log.info("klineClient.getCoinPrice：{}", response);
            CoingeckoPriceModel data = response.getData();
            if (response.getCode() != 0 || data == null) {
                log.error("[log---alarm]getCoinPrice failed, code = {}", response.getCode());
            } else {
                KLINE_PRICE_MAP.put(coin, data.getPrice());
            }
        } catch (Exception e) {
            log.error("[log---alarm]getCoinPrice Exception: {} ", e.getMessage());
        }
    }

    /**
     * 获取指定币种kline价格，单位usd
     *
     * @param key
     */
    @Override
    public BigDecimal getPrice(String key) {
        if (!KLINE_PRICE_MAP.containsKey(key)) {
            fetchCoinPrice(key);
        }
        return KLINE_PRICE_MAP.get(key);
    }
}
