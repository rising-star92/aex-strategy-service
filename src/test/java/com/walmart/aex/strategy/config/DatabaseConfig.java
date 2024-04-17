package com.walmart.aex.strategy.config;

import com.walmart.aex.strategy.properties.CredProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource h2DataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean
    @Qualifier("strategyJdbcTemplate")
    public NamedParameterJdbcTemplate strategyJdbcTemplate(CredProperties credProperties)  {
        return new NamedParameterJdbcTemplate(DataSourceBuilder.create().build());
    }

}
