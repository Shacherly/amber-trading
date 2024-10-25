package com.google.backend.trading.client.feign;

import com.google.backend.asset.client.feign.AssetRiskApi;
import com.google.backend.trading.client.fallback.AssetRiskInfoClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author trading
 * @date 2021/10/12 11:27
 */
@FeignClient(name = "assetRiskInfoClient", url = "${asset.host}", configuration = FeignConfig.class, fallbackFactory =
		AssetRiskInfoClientFallbackFactory.class)
public interface AssetRiskInfoClient extends AssetRiskApi {

}
