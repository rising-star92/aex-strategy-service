package com.walmart.aex.strategy.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "executorConfig")
public interface ExecutorProperties {

    @Property(propertyName = "fixedThreadPool.count")
    int getFixedThreadPoolCount();

}
