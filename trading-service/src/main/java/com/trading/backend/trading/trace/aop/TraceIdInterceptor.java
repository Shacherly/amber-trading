package com.google.backend.trading.trace.aop;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.trace.TraceIdAttribute;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author trading
 * @date 2021/5/12 17:54
 */
@Slf4j
public class TraceIdInterceptor implements MethodInterceptor {


    private TraceIdAttributeSource traceIdAttributeSource;

    public void setTraceIdAttributeSource(TraceIdAttributeSource traceIdAttributeSource) {
        this.traceIdAttributeSource = traceIdAttributeSource;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : invocation.getClass());

        TraceIdAttribute attribute = traceIdAttributeSource.getTraceIdAttribute(method, targetClass);
        if (null == attribute) {
            return invocation.proceed();
        }

        boolean clearTraceId = true;
        try {
            boolean existTraceId = null != MDC.get(Constants.TRACE_SPAN_ID);
            // not override and exist trace id
            if (!attribute.isOverride() && existTraceId) {
                clearTraceId = false;
                skipLog(method);
            } else {
                MDC.put(Constants.TRACE_SPAN_ID, UUID.randomUUID().toString());
                startLog(existTraceId, method);
            }
            return invocation.proceed();
        } finally {
            if (clearTraceId) {
                endLog(method);
                MDC.remove(Constants.TRACE_SPAN_ID);
            }
        }

    }

    private void skipLog(Method method) {
        if (log.isTraceEnabled()) {
            log.trace("trace intercept method = {}, exist trace id, skip", method.getName());
        }
    }

    private void startLog(boolean existTraceId, Method method) {
        if (log.isTraceEnabled()) {
            if (existTraceId) {
                log.trace("trace intercept method = {}, override trace id", method.getName());
            } else {
                log.trace("trace intercept method = {}, set trace id", method.getName());
            }
        }
    }

    private void endLog(Method method) {
        if (log.isTraceEnabled()) {
            log.trace("finish trace intercept method = {}, remove trace id", method.getName());
        }
    }

}
