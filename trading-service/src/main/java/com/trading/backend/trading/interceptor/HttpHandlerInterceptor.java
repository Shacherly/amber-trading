package com.google.backend.trading.interceptor;

import com.google.backend.trading.config.TradeProperties;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trading
 * @date 2021/12/3 10:38
 */
public class HttpHandlerInterceptor implements HandlerInterceptor {

	private TradeProperties properties;

	public HttpHandlerInterceptor(TradeProperties properties) {
		this.properties = properties;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LocaleContextHolder.getLocale();
		if (properties.getTrafficDisabledControl().isEnabled()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		return true;
	}
}
