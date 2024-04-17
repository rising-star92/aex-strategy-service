package com.walmart.aex.strategy.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "featureConfig")
public interface FeatureConfigProperties {
    @Property(propertyName = "is.save.default.volume.deviation.enabled")
    boolean getSaveDefaultVolumeDeviation();

    @Property(propertyName = "sp.save.total.size.percent.enabled")
    boolean getTotalSizePercentFeature();
}
