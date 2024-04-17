package com.walmart.aex.strategy.properties;


import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "programFixtureConfig")
public interface ProgramFixtureProperties {
    @Property(propertyName = "csa.base.url")
    String getCsaBaseUrl();

}
