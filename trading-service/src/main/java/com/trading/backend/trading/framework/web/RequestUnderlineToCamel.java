package com.google.backend.trading.framework.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 处理GET请求 form 或 request param 的实体对象下划线转驼峰的命名映射
 *
 * @author trading
 * @date 2021/10/10 16:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface RequestUnderlineToCamel {


}
