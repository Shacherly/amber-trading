package com.google.backend.trading.client.feign;


import com.google.backend.asset.common.model.base.BaseReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.fallback.AssetOpsClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author trading
 * @date 2021/8/19 17:13
 */
@RequestMapping("/internal/v1/ops")
@FeignClient(name = "assetOpsClient", url = "${asset.host}", configuration = FeignConfig.class,
        fallbackFactory = AssetOpsClientFallbackFactory.class)
public interface AssetOpsClient {
    @PostMapping("/rollback")
    @ApiOperation(value = "回滚接口", notes = "仅回滚用户资金及流水状态,相关统计类信息不会发生相应回滚,如用户最小活期余额.")
    Response<?> doRollback(@RequestBody @Validated BaseReq req);
}
