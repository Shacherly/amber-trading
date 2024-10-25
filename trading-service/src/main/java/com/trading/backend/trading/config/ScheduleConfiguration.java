package com.google.backend.trading.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 暂时使用框架默认的线程池来调度，不进行额外配置
 *
 * @author trading
 * @date 2021/10/11 15:40
 */
@Slf4j
@Configuration
@EnableScheduling
public class ScheduleConfiguration {

}
