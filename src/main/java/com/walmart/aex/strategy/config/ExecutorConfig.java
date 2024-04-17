package com.walmart.aex.strategy.config;


import com.walmart.aex.strategy.properties.ExecutorProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ExecutorConfig {

    private ExecutorService executorService;

    @ManagedConfiguration
    private ExecutorProperties executorProperties;

    @Bean
    public ExecutorService executorService() {
        executorService = Executors.newFixedThreadPool(Optional.ofNullable(executorProperties)
                .map(ExecutorProperties::getFixedThreadPoolCount).orElse(5));
        return executorService;
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying executor service...");
        executorService.shutdown();
    }

}
