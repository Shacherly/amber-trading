package com.google.backend.trading.trace.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author trading
 * @date 2021/5/13 11:19
 */
public abstract class TraceIdAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    protected TraceIdAttributeSourcePointcut() {
        setClassFilter(ClassFilter.TRUE);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        TraceIdAttributeSource tas = getTraceIdAttributeSource();
        return (tas == null || tas.getTraceIdAttribute(method, targetClass) != null);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TraceIdAttributeSourcePointcut)) {
            return false;
        }
        TraceIdAttributeSourcePointcut otherPc = (TraceIdAttributeSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getTraceIdAttributeSource(), otherPc.getTraceIdAttributeSource());
    }

    @Override
    public int hashCode() {
        return TraceIdAttributeSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getTraceIdAttributeSource();
    }


    /**
     * Obtain the underlying TraceIdAttributeSource (may be {@code null}).
     * To be implemented by subclasses.
     * @return
     */
    @Nullable
    protected abstract TraceIdAttributeSource getTraceIdAttributeSource();
}
