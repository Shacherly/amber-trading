package com.google.backend.trading.trace.annotation;


import com.google.backend.trading.trace.TraceIdAttribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * parse to {@link TraceIdAttribute}
 * @author trading
 * @date 2021/5/12 17:50
 */
@Inherited
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TraceId {

    boolean override() default false;
}
