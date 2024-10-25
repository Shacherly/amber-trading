package com.google.backend.trading;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.google.backend.trading.config.FeignConfiguration;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.StringUtils;

import java.util.TimeZone;

/**
 * @author trading
 * @date 2021/9/27 10:26
 */
@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableSpringUtil
public class TradingServiceApplication {

    public static void main(String[] args) {
        try {
            //必须优先于SWHystrixConcurrencyStrategyWrapper设置strategy
            HystrixPlugins.getInstance().registerConcurrencyStrategy(new FeignConfiguration.MdcHystrixConcurrencyStrategy());
            //设置全局DEFAULT时区
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
            ConfigurableApplicationContext context = SpringApplication.run(TradingServiceApplication.class, args);
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            AlarmLogUtil.alarm("TradingServiceApplication start, activeProfiles = {}",
                    StringUtils.arrayToCommaDelimitedString(activeProfiles));
            context.addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> log.info("TradingServiceApplication " +
                    "closing"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
