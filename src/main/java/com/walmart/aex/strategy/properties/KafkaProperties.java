package com.walmart.aex.strategy.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "kafkaConfig")
public interface KafkaProperties {
    @Property(propertyName = "aex.strategy.service.kafka.topic")
    String getStrategyServiceTopic();


    @Property(propertyName = "aex.strategy.service.kafka.server")
    String getStrategyServiceKafkaServer();


}
