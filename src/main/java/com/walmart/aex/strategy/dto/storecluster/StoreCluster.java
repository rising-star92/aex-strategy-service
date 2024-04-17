package com.walmart.aex.strategy.dto.storecluster;

import lombok.Data;

import java.util.List;

@Data
public class StoreCluster {

    private String appName;

    private String eventId;

    private String clusterType;

    private ClusterAttributes clusterAttributes;

    private List<Integer> sourceStoreList;

    private String userId;

    private List<Cluster> clusters;

}
