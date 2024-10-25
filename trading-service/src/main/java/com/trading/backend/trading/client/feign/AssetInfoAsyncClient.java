package com.google.backend.trading.client.feign;

import com.google.backend.asset.common.model.asset.req.PoolReq;
import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.common.web.Response;
import com.google.backend.trading.config.feign.DefaultFeignConfig;
import feign.RequestLine;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author trading
 * @date 2021/10/23 11:04
 */
@FeignClient(name = "assetInfoAsyncClient", url = "${asset.host}", configuration = DefaultFeignConfig.class)
public interface AssetInfoAsyncClient {

	@RequestLine(value = "POST /internal/v1/asset-info/get-pool")
	@ApiOperation(value = "获取用户的资产池数据接口")
	CompletableFuture<Response<Map<String, PoolEntity>>> getPool(@RequestBody @Valid PoolReq req);
}
