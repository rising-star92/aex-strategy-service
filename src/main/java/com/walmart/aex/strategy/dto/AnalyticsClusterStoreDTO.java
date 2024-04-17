package com.walmart.aex.strategy.dto;

import lombok.Data;

@Data
public class AnalyticsClusterStoreDTO {
    private Long programId;
    private Integer analyticsClusterId;
    private String stateProvinceCode;
    private Long storeCount;

    public AnalyticsClusterStoreDTO(Long programId, Integer analyticsClusterId, String stateProvinceCode, Long storeCount ){
        this.programId = programId;
        this.analyticsClusterId = analyticsClusterId;
        this.stateProvinceCode = stateProvinceCode;
        this.storeCount = storeCount;
    }

    public AnalyticsClusterStoreDTO(Integer analyticsClusterId, String stateProvinceCode, Long storeCount ){
        this.analyticsClusterId = analyticsClusterId;
        this.stateProvinceCode = stateProvinceCode;
        this.storeCount = storeCount;
    }
}
