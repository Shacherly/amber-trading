package com.google.backend.trading.migrate;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * APP数据迁移脚本
 *
 * @author trading
 * @date 2021/11/9 21:11
 */
@Slf4j
@Profile({"disuse"})
@Configuration
public class AppMigrateConfig {

	@Autowired
	private AppMigrateProperties properties;

	@Bean
	public DataSource appMigrateDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUsername(properties.pgsql.getUsername());
		druidDataSource.setPassword(properties.pgsql.getPassword());
		druidDataSource.setUrl(properties.pgsql.getUrl());
		druidDataSource.setInitialSize(5);
		druidDataSource.setMinIdle(5);
		druidDataSource.setMaxActive(5);
		druidDataSource.setMaxWait(5000);
		druidDataSource.setDriverClassName("org.postgresql.Driver");
		return druidDataSource;
	}

	@Bean
	public JdbcTemplate appMigrateJdbcTemplate() {
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(appMigrateDataSource());
		template.setDatabaseProductName("app-migrate");
		return template;
	}

	@Bean
	@Primary
	public JdbcTemplate jdbcTemplate(DataSource dataSource, JdbcProperties properties) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JdbcProperties.Template template = properties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
		}
		return jdbcTemplate;
	}


	@Data
	@Component
	@ConfigurationProperties(prefix = "app-migrate")
	public static class AppMigrateProperties {

		private Pgsql pgsql;

	}

	@Data
	public static class Pgsql {

		private String url;

		private String username;

		private String password;
	}
}
