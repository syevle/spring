package com.example;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@SpringBootApplication
public class Api {

	private final XADataSourceWrapper wrapper;

	public static void main(String[] args) {
		SpringApplication.run(Api.class, args);
	}

	@Bean
	@ConfigurationProperties(prefix = "b")
	public DataSource b() throws Exception {
		return xaDataSource("b");
	}

	@Bean
	@ConfigurationProperties(prefix = "a")
	public DataSource a() throws Exception {
		return xaDataSource("a");
	}

	private DataSource xaDataSource(String name) throws Exception {
		JdbcDataSource xaDataSource = new JdbcDataSource();
		xaDataSource.setUrl("jdbc:h2:mem:" + name);
//		xaDataSource.setUrl("jdbc:h2:./" + name);
		xaDataSource.setUser("sa");
		xaDataSource.setPassword("");
		return this.wrapper.wrapDataSource(xaDataSource);
	}

	public Api(XADataSourceWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Bean
	public DataSourceInitializer bInit(
			@Qualifier("b") DataSource b,
			@Value("classpath:b.sql") Resource r) {
		return init(b, r);
	}

	@Bean
	public DataSourceInitializer aInit(
			@Qualifier("a") DataSource a,
			@Value("classpath:a.sql") Resource r) {
		return init(a, r);
	}

	private DataSourceInitializer init(DataSource ds, Resource r) {
		DataSourceInitializer dsi = new DataSourceInitializer();
		dsi.setDatabasePopulator(new ResourceDatabasePopulator(r));
		dsi.setDataSource(ds);
		return dsi;
	}
}



