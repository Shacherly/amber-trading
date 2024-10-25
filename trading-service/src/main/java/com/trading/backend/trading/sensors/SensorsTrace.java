package com.google.backend.trading.sensors;

import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;

/**
 * @author trading
 */
@Slf4j
public class SensorsTrace implements DisposableBean {

    private final SensorsAnalytics sensorsAnalytics;

    public SensorsTrace(SensorsAnalytics sensorsAnalytics) {
        this.sensorsAnalytics = sensorsAnalytics;
    }

    /**
     * 写入本地日志，使用logAgent来处理
     *
     * @param uid
     * @param event
     * @param properties
     */
    public void track(String uid, String event, java.util.Map<String, Object> properties) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("SensorsTrace event = {}, properties = {}", event, properties);
            }
            sensorsAnalytics.track(uid, true, event, properties);
            sensorsAnalytics.flush();
        } catch (Exception e) {
            log.error("Sensors track exception = {}", e.getMessage());
        }
    }

    /**
     * 设置用户属性
     *
     * @param uid
     * @param properties
     */
    public void profileSetOnce(String uid, Map<String, Object> properties) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("SensorsTrace properties = {}", properties);
            }
            sensorsAnalytics.profileSetOnce(uid, true, properties);
            sensorsAnalytics.flush();
        } catch (Exception e) {
            log.error("Sensors track exception = {}", e.getMessage());
        }
    }

    /**
     * 设置用户属性
     *
     * @param uid
     * @param property
     * @param value
     */
    public void profileSetOnce(String uid, String property, Object value) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("SensorsTrace property = {} value={}", property, value);
            }
            sensorsAnalytics.profileSetOnce(uid, true, property, value);
            sensorsAnalytics.flush();
        } catch (Exception e) {
            log.error("Sensors track exception = {}", e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        sensorsAnalytics.shutdown();
    }
}
