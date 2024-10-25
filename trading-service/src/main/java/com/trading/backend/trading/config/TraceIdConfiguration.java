package com.google.backend.trading.config;

import com.google.backend.trading.trace.aop.TraceIdAnnotationAdvisor;
import com.google.backend.trading.trace.aop.TraceIdAttributeSource;
import com.google.backend.trading.trace.aop.TraceIdInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

/**
 * @author trading
 * @date 2021/5/12 19:12
 */
@Slf4j
@Configuration
public class TraceIdConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TraceIdAnnotationAdvisor transactionAdvisor(
            TraceIdAttributeSource traceIdAttributeSource, TraceIdInterceptor traceInterceptor) {
        TraceIdAnnotationAdvisor advisor = new TraceIdAnnotationAdvisor();
        advisor.setTraceIdAttributeSource(traceIdAttributeSource);
        advisor.setAdvice(traceInterceptor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TraceIdAttributeSource traceIdAttributeSource() {
        return new TraceIdAttributeSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TraceIdInterceptor traceIdInterceptor(TraceIdAttributeSource traceIdAttributeSource) {
        TraceIdInterceptor interceptor = new TraceIdInterceptor();
        interceptor.setTraceIdAttributeSource(traceIdAttributeSource);
        return interceptor;
    }
}
