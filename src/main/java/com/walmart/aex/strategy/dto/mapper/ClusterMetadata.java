package com.walmart.aex.strategy.dto.mapper;

import com.walmart.aex.strategy.dto.SizeCluster;
import com.walmart.aex.strategy.entity.StrategyCcSPClusId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterMetadata {
    private SizeCluster sizeCluster;
    private HashMap<StrategyCcSPClusId, Set<Integer>> mapCCSPClusIdByEligibleSizeIds;

}
