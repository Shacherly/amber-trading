package com.google.backend.trading.trace.aop;

import com.google.backend.trading.trace.TraceIdAttribute;
import com.google.backend.trading.trace.annotation.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trading
 * @date 2021/5/13 11:23
 */
@Slf4j
public class TraceIdAttributeSource {

    private static final TraceIdAttribute NULL_ATTRIBUTE = new TraceIdAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };

    private final Map<Object, TraceIdAttribute> attributeCache = new ConcurrentHashMap<>(1024);

    public TraceIdAttribute getTraceIdAttribute(Method method, Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        // First, see if we have a cached value.
        Object cacheKey = getCacheKey(method, targetClass);
        TraceIdAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            if (cached == NULL_ATTRIBUTE) {
                return null;
            }
            else {
                return cached;
            }
        }
        else {
            // We need to work it out.
            TraceIdAttribute tiAttr = parseTraceIdAnnotation(method, targetClass);
            // Put it in the cache.
            if (tiAttr == null) {
                this.attributeCache.put(cacheKey, NULL_ATTRIBUTE);
            }
            else {
                String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
                if (log.isTraceEnabled()) {
                    log.trace("Adding traceId method '" + methodIdentification + "' with attribute: " + tiAttr);
                }
                this.attributeCache.put(cacheKey, tiAttr);
            }
            return tiAttr;
        }
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    private TraceIdAttribute parseTraceIdAnnotation(Method method, Class<?> targetClass) {
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        AnnotationAttributes attributes = AnnotatedElementUtils
                .findMergedAnnotationAttributes(specificMethod, TraceId.class, false, false);
        if (null == attributes) {
            attributes = AnnotatedElementUtils
                    .findMergedAnnotationAttributes(specificMethod.getDeclaringClass(), TraceId.class, false, false);
        }
        if (null == attributes) {
            return null;
        }
        TraceIdAttribute attribute = new TraceIdAttribute();
        attribute.setOverride(attributes.getBoolean("override"));
        return attribute;
    }
}
