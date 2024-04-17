package com.walmart.aex.strategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import io.strati.ccm.utils.client.logger.LogLevel;
import io.strati.ccm.utils.client.logger.Logger;
import io.strati.ccm.utils.client.logger.LoggerFactory;

@ComponentScan("com.walmart.platform.txn.springboot.filters")
@ComponentScan("com.walmart.platform.txn.springboot.interceptor")
@SpringBootApplication(scanBasePackages = {
        "com.walmart.aex.strategy",
        "io.strati.tunr.utils.client"
})
@EnableCaching
public class Application {
    static Logger logger = LoggerFactory.getInstance().getLogger();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.setLogLevel(LogLevel.ERROR);
    }
}

