package com.google.backend.trading.trace.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

/**
 * @author trading
 * @date 2021/5/12 17:48
 */
public class TraceIdAnnotationAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private TraceIdAttributeSource traceIdAttributeSource;

    private final TraceIdAttributeSourcePointcut pointcut = new TraceIdAttributeSourcePointcut() {
        @Override
        @Nullable
        protected TraceIdAttributeSource getTraceIdAttributeSource() {
            return traceIdAttributeSource;
        }
    };

    public void setTraceIdAttributeSource(TraceIdAttributeSource traceIdAttributeSource) {
        this.traceIdAttributeSource = traceIdAttributeSource;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return super.getAdvice();
    }
}
