package com.google.backend.trading.client.fallback;

import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AlarmClient;
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
public class AlarmClientFallbackFactory implements FallbackFactory<AlarmClient> {
    @Override
    public AlarmClient create(Throwable throwable) {
        String message = ExceptionUtils.getRootCauseMessage(throwable);
        AlarmLogUtil.alarm("AlarmClient fallback, cause = {}", message, throwable);
        return req -> Response.fail(BusinessExceptionEnum.ALARM_FALLBACK.getCode(), message);
    }
}