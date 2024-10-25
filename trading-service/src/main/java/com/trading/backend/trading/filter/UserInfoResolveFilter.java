package com.google.backend.trading.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author trading
 * @date 2021/10/10 15:36
 */
@Slf4j
public class UserInfoResolveFilter implements Filter {

	private static final ThreadLocal<ObjectMapper> OM = ThreadLocal.withInitial(() -> {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	});

	public static ObjectMapper getObjectMapper() {
		return OM.get();
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest rawRequest = (HttpServletRequest) request;
		String userInfoJson = rawRequest.getHeader(Constants.X_GW_USER_HEADER);
		if (log.isDebugEnabled()) {
			log.debug("from header {}, user info = {}", Constants.X_GW_USER_HEADER, userInfoJson);
		}
		if (!StringUtils.isEmpty(userInfoJson)) {
			UserInfo userInfo = getObjectMapper().readValue(userInfoJson, UserInfo.class);
			request.setAttribute(UserInfo.CURRENCY_USER_TAG, userInfo);
		}
		filterChain.doFilter(request, response);
		request.removeAttribute(UserInfo.CURRENCY_USER_TAG);
	}
}
