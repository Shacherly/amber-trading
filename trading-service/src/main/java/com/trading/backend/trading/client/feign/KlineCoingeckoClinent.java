package com.google.backend.trading.client.feign;

import com.google.backend.common.web.Response;
import com.google.backend.trading.config.feign.FeignConfig;
import com.google.backend.trading.model.kline.coingecko.dto.CoingeckoPriceModel;
import com.google.backend.trading.model.kline.coingecko.dto.PriceModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author david.chen
 * @date 2022/2/17 16:24
 */
@FeignClient(name = "klineCoingeckoClinent", url = "${kline-coingecko.host}", configuration = FeignConfig.class)
public interface KlineCoingeckoClinent {


    @GetMapping({"/v1/price/batch"})
    Response<List<PriceModel>> getPriceBatch();

    @GetMapping({"/v1/coingecko/latest"})
    Response<CoingeckoPriceModel> getCoinPrice(@RequestParam String token);
}
