package com.walmart.aex.strategy.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "rfaGraphQlConfig")
public interface RFAGraphQLProperties {
	
	@Property(propertyName = "rfa.space.assortproduct.url")
    String getAssortProductUrl();

    @Property(propertyName = "rfa.space.assortproduct.fineline.query")
    String getAssortProductRFAFinelineQuery();
    
    @Property(propertyName = "rfa.space.assortproduct.stylecc.query")
    String getAssortProductRFAStyleCcQuery();

    @Property(propertyName = "rfa.space.assortproduct.consumer.id")
    String getAssortProductConsumerId();

    @Property(propertyName = "rfa.space.assortproduct.consumer.name")
    String getAssortProductConsumerName();

    @Property(propertyName = "rfa.space.assortproduct.consumer.env")
    String getAssortProductConsumerEnv();
}
