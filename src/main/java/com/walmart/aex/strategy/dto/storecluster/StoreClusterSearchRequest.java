package com.walmart.aex.strategy.dto.storecluster;

import lombok.Data;

@Data
public class StoreClusterSearchRequest {

    private String appName;

    private String eventId;

    private String clusterType;

    private ClusterAttributes clusterAttributes;

}
