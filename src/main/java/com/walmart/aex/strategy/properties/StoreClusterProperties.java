package com.walmart.aex.strategy.properties;


import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "storeClusterConfig")
public interface StoreClusterProperties {
    @Property(propertyName = "store.cluster.base.url")
    String getStoreClusterBaseUrl();

}
