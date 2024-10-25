package com.google.backend.trading.client.feign;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.util.AlarmLogUtil;
import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author trading
 * @date 2021/10/26 16:22
 */
@Slf4j
public class ErrorLogFeignClient extends Client.Default {


	private static final ThreadLocal<ObjectMapper> OM = ThreadLocal.withInitial(() -> {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	});

	public ErrorLogFeignClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
		super(sslContextFactory, hostnameVerifier);
	}

	@Override
	public Response execute(Request request, Request.Options options) throws IOException {
		Response response;
		long startTime = System.currentTimeMillis();
		try {
			response = super.execute(request, options);
		} catch (IOException e) {
			AlarmLogUtil.alarm("feign-client-request-trace, io exception, cost time = {}, req = {}, cause = {}",
					costTime(startTime),
					request, ExceptionUtils.getRootCauseMessage(e));
			throw e;
		}
		if (HttpStatus.OK.value() != response.status()) {
			return response;
		}
		InputStream bodyStream = response.body().asInputStream();
		String responseBody = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
		JsonNode jsonNode;
		try {
			jsonNode = OM.get().readTree(responseBody);
		} catch (Exception e) {
			log.error("parse response body err, cause = {}", ExceptionUtils.getRootCauseMessage(e));
			return response.toBuilder().body(responseBody, StandardCharsets.UTF_8).build();
		}
		int code = jsonNode.get("code").asInt(-99);
		long costTime = costTime(startTime);
		if (BusinessExceptionEnum.SUCCESS.getCode() != code) {
			log.error("feign-client-request-trace, code failure, cost time = {}, req = {}, res = {}",
					costTime, request, jsonNode);
		} else {
			log.info("feign-client-request-trace, code success, cost time = {}, req = {}, res = {}",
					costTime, request, jsonNode);
		}
		if (costTime > Duration.ofSeconds(5).toMillis()) {
			AlarmLogUtil.alarm("feign-client-request-trace, duration too long, cost time = {}", costTime);
		}
		return response.toBuilder().body(responseBody, StandardCharsets.UTF_8).build();
	}

	private long costTime(long startTime) {
		return System.currentTimeMillis() - startTime;
	}
}
