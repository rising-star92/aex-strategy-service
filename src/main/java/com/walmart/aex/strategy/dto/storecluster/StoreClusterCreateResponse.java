package com.walmart.aex.strategy.dto.storecluster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StoreClusterCreateResponse {

    private String clusterGroupId;

    @JsonProperty(value = "isSuccess")
    private boolean isSuccess;

    private List<Cluster> clusters;

}
