package com.walmart.aex.strategy.dto.storecluster;

import lombok.Data;

import java.util.List;

@Data
public class Cluster {

    private String clusterId;

    private String clusterName;

    private List<Integer> storeList;

}
