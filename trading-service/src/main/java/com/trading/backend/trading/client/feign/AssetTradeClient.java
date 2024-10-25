package com.google.backend.trading.client.feign;

import com.google.backend.asset.client.feign.AssetTradeApi;
import com.google.backend.trading.client.fallback.AssetTradeClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author trading
 * @date 2021/8/17 14:37
 */
@FeignClient(name = "assetTradeClient", url = "${asset.host}", configuration = FeignConfig.class, fallbackFactory =
		AssetTradeClientFallbackFactory.class)
public interface AssetTradeClient extends AssetTradeApi {

}
