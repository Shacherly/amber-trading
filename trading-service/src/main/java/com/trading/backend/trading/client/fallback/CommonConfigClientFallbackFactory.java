package com.google.backend.trading.client.fallback;

import com.google.backend.trading.client.feign.CommonConfigClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.AllConfig;
import com.google.backend.trading.util.AlarmLogUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * @author trading
 * @date 2021/11/6 10:54
 */
@Slf4j
@Component
public class CommonConfigClientFallbackFactory implements FallbackFactory<CommonConfigClient> {
	@Override
	public CommonConfigClient create(Throwable cause) {
		AlarmLogUtil.alarm("CommonConfigClient fallback, cause = {}", ExceptionUtils.getRootCauseMessage(cause), cause);
		return new CommonConfigClient() {
			@Override
			public Response<AllConfig> configInfoByType(Integer type) {
				return Response.fail(BusinessExceptionEnum.COMMON_CONFIG_FALLBACK.getCode());
			}

			@Override
			public Response<AllConfig> allConfigInfo() {
				return Response.fail(BusinessExceptionEnum.COMMON_CONFIG_FALLBACK.getCode());
			}
		};
	}
}
