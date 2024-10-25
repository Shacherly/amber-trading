package com.google.backend.trading.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author trading
 * @date 2021/9/29 20:37
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.google.backend.trading.dao.mapper")
public class MybatisConfig {


	@Bean(initMethod = "init")
	@Primary
	public DataSource dataSource() {
		log.info("Init DruidDataSource");
		return DruidDataSourceBuilder.create().build();
	}
}
