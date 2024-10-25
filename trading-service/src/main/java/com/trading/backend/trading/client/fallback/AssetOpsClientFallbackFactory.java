package com.google.backend.trading.client.fallback;

import com.google.backend.asset.common.model.base.BaseReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetOpsClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.util.AlarmLogUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * @author trading
 */
@Slf4j
@Component
public class AssetOpsClientFallbackFactory implements FallbackFactory<AssetOpsClient> {
    @Override
    public AssetOpsClient create(Throwable throwable) {
        AlarmLogUtil.alarm("AssetOpsClient fallback, cause = {}", ExceptionUtils.getRootCauseMessage(throwable), throwable);
        return new AssetOpsClient() {
            @Override
            public Response<?> doRollback(BaseReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }
        };
    }
}