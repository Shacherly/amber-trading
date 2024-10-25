package com.google.backend.trading.client.fallback;

import com.google.backend.asset.common.model.asset.req.BatchUserPoolReq;
import com.google.backend.asset.common.model.asset.req.RiskUserPoolReq;
import com.google.backend.asset.common.model.base.BatchPoolEntityForRisk;
import com.google.backend.common.web.PageResult;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetRiskInfoClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.util.AlarmLogUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author trading
 * @date 2021/11/6 10:54
 */
@Slf4j
@Component
public class AssetRiskInfoClientFallbackFactory implements FallbackFactory<AssetRiskInfoClient> {
	@Override
	public AssetRiskInfoClient create(Throwable cause) {
		AlarmLogUtil.alarm("AssetRiskInfoClient fallback, cause = {}", ExceptionUtils.getRootCauseMessage(cause), cause);
		return new AssetRiskInfoClient() {
			@Override
			public Response<List<BatchPoolEntityForRisk>> getPoolBatch(BatchUserPoolReq req) {
				return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
			}

			@Override
			public Response<PageResult<BatchPoolEntityForRisk>> getRiskUserPool(RiskUserPoolReq req) {
				return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
			}
		};
	}
}
