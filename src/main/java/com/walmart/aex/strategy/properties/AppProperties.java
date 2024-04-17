package com.walmart.aex.strategy.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

import java.util.List;

@Configuration(configName = "appConfig")
public interface AppProperties {
    @Property(propertyName = "weatherClusterStrategyId")
    Integer weatherClusterStrategyId();

    @Property(propertyName = "fixtureStrategyId")
    Integer fixtureStrategyId();

    @Property(propertyName = "getCluster1OffshoreList")
    List<String> getCluster1OffshoreList();

    @Property(propertyName = "getCluster2OffshoreList")
    List<String> getCluster2OffshoreList();

    @Property(propertyName = "getCluster7OffshoreList")
    List<String> getCluster7OffshoreList();

    @Property(propertyName = "getStrategyGroupTypes")
    String getStrategyGroupTypes();

    @Property(propertyName = "ahsUrl")
    String getAHSUrl();

    @Property(propertyName = "ahsConsumerId")
    String getAHSConsumerId();

    @Property(propertyName = "ahsEnv")
    String getAHSEnv();

    @Property(propertyName = "ahsAttributeTypes")
    List<String> getAHSAttributeTypes();

    @Property(propertyName = "csaUrl")
    String getCSAUrl();

    @Property(propertyName = "csaEnv")
    String getCSAEnv();

    @Property(propertyName = "ap.s1.release.flag")
    String getAPS1ReleaseFlag();

    @Property(propertyName = "ap.s4.release.flag")
    String getAPS4ReleaseFlag();

    @Property(propertyName = "size.pack.release.flag")
    String getSPReleaseFlag();

    @Property(propertyName = "strategy.aptbls.release.flag")
    String getAPReleaseFlag();

    @Property(propertyName = "size.pack.spread.feature.flag")
    String getSPSpreadFeatureFlag();

    @Property(propertyName = "vd.analytics.clusterGroupDesc")
    String getVolumeDeviationAnalyticsClusterGroupDesc();

    @Property(propertyName = "csaSpaceType")
    List<String> getCsaSpaceType();

    @Property(propertyName = "csaProgramsPlanId")
    List<String> getCsaProgramsPlanId();

    @Property(propertyName = "planDefinitionUrl")
    String getPlanDefinitionUrl();
}
