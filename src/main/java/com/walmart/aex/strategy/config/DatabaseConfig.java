package com.walmart.aex.strategy.config;

import com.walmart.aex.strategy.properties.DatabaseProperties;
import com.walmart.aex.strategy.properties.CredProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@Slf4j
@Profile("!test")
public class DatabaseConfig {

    @ManagedConfiguration
    private DatabaseProperties databaseProperties;


    @Bean
    public DataSource dataSource(CredProperties credProperties) throws IOException {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(databaseProperties.getDriver());
        config.setJdbcUrl(databaseProperties.getUrl());
        config.setUsername(credProperties.fetchSQLServerUserName());
        config.setPassword(credProperties.fetchSQLServerPassword());
        log.info("DataSource Properties Fetched Successfully ");
        return new HikariDataSource(config);
    }

    @Bean
    @Qualifier("strategyJdbcTemplate")
    @Primary
    public NamedParameterJdbcTemplate strategyJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.walmart.aex.strategy.entity");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    private final Properties additionalProperties() {
        final Properties properties = new Properties();
        properties.setProperty("spring.jpa.hibernate.naming.physical-strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
        properties.put("hibernate.jdbc.batch_size", "50");
        properties.put("hibernate.order_updates", "true");
        properties.put("hibernate.order_inserts", "true");
        properties.setProperty("spring.jpa.show-sql", "true");
        properties.setProperty("spring.jpa.properties.hibernate.format_sql", "true");
        return properties;
    }

}

