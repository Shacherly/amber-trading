package com.google.backend.trading.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trading
 * @date 2021/3/17 19:45
 */
@Configuration
@EnableKnife4j
@EnableSwagger2
@Profile({"local", "dev", "sit"})
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				// 加了ApiOperation注解的类，才生成接口文档
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class).and(RequestHandlerSelectors.basePackage("com.google.backend.trading.controller")))
				// 路径配置
				.paths(PathSelectors.any()).build().directModelSubstitute(java.util.Date.class, String.class)
				.globalRequestParameters(getParameterList());
	}

	private List<RequestParameter> getParameterList() {
		List<RequestParameter> list = new ArrayList<>();
		RequestParameterBuilder builder = new RequestParameterBuilder();
		builder.name("x-gw-user").description("正常是客户端传access_token和refresh_token来鉴权交换用户信息并由网关传递给后端服务，联调或测试的时候可以手动来模拟设置值来进行")
				.in(ParameterType.HEADER)
				.query(specificationBuilder -> specificationBuilder
						.model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING))
						.defaultValue("{\"user_id\":\"616289a2d4b1a6d195d6f286\",\"kyc_status\":\"0\"}"));
		RequestParameter parameter = builder.build();
		list.add(parameter);
		return list;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				// 定义浏览器title
				.title("交易服务")
				// 描述
				.description("交易服务接口文档")
				// 文档请求host地址
//            .termsOfServiceUrl("")
				// api版本
				.version("0.0.1").build();
	}
}