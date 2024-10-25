package com.google.backend.trading.framework.web;

import com.google.backend.trading.model.user.UserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author trading
 * @date 2021/10/10 15:32
 */
@Component
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(UserInfo.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Object object = webRequest.getAttribute(UserInfo.CURRENCY_USER_TAG, RequestAttributes.SCOPE_REQUEST);
		if (!(object instanceof UserInfo)) {
			return null;
		}
		return object;
	}
}
