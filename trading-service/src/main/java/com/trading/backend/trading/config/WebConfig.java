package com.google.backend.trading.config;

import com.google.backend.trading.config.i18n.I18nAcceptHeaderLocaleResolver;
import com.google.backend.trading.config.web.CustomDateWebBindingInitializer;
import com.google.backend.trading.filter.AccessOncePerRequestFilter;
import com.google.backend.trading.filter.SensorsPushParamFilter;
import com.google.backend.trading.filter.UserInfoResolveFilter;
import com.google.backend.trading.framework.web.UnderlineToCamelArgumentResolver;
import com.google.backend.trading.framework.web.UserInfoArgumentResolver;
import com.google.backend.trading.interceptor.HttpHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * @author trading
 */
@Configuration
public class WebConfig {
	@Autowired
	private RequestMappingHandlerAdapter handlerAdapter;

//	/**
//	 * 增加字符串转日期的功能
//	*/
//	@PostConstruct
//	public void initEditableAvlidation() {
//
//		ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)handlerAdapter.getWebBindingInitializer();
//		if(initializer.getConversionService()!=null) {
//			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//			GenericConversionService genericConversionService = (GenericConversionService)initializer.getConversionService();
//			genericConversionService.addConverter(new StringToDateConverter());
//		}
//	}
	@Autowired
	public void setWebBindingInitializer(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
		requestMappingHandlerAdapter.setWebBindingInitializer(new CustomDateWebBindingInitializer());
	}
	@Bean
	public FilterRegistrationBean<AccessOncePerRequestFilter> reqResFilter() {
		FilterRegistrationBean<AccessOncePerRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		AccessOncePerRequestFilter filter = new AccessOncePerRequestFilter();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.addUrlPatterns("*");
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<UserInfoResolveFilter> userInfoResolveFilter() {
		FilterRegistrationBean<UserInfoResolveFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		UserInfoResolveFilter filter = new UserInfoResolveFilter();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.addUrlPatterns("*");
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<SensorsPushParamFilter> sensorsPushParamFilter() {
		FilterRegistrationBean<SensorsPushParamFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		SensorsPushParamFilter filter = new SensorsPushParamFilter();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.addUrlPatterns("*");
		return filterRegistrationBean;
	}

	@Bean
	public HttpHandlerInterceptor httpHandlerInterceptor(TradeProperties properties) {
		return new HttpHandlerInterceptor(properties);
	}

	@Configuration
	public static class WebMvcConfiguration implements WebMvcConfigurer {

		private final UnderlineToCamelArgumentResolver underlineToCamelArgumentResolver;

		private final UserInfoArgumentResolver userInfoArgumentResolver;

		private final HttpHandlerInterceptor httpHandlerInterceptor;

		public WebMvcConfiguration(UnderlineToCamelArgumentResolver underlineToCamelArgumentResolver,
								   UserInfoArgumentResolver userInfoArgumentResolver,
								   HttpHandlerInterceptor httpHandlerInterceptor) {
			this.underlineToCamelArgumentResolver = underlineToCamelArgumentResolver;
			this.userInfoArgumentResolver = userInfoArgumentResolver;
			this.httpHandlerInterceptor = httpHandlerInterceptor;
		}

		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
			resolvers.add(underlineToCamelArgumentResolver);
			resolvers.add(userInfoArgumentResolver);
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(httpHandlerInterceptor).addPathPatterns("/**");
		}
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new I18nAcceptHeaderLocaleResolver();
	}
}