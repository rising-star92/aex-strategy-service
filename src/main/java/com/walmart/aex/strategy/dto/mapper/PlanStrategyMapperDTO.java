package com.walmart.aex.strategy.dto.mapper;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.PlanClusterStrategy;
import com.walmart.aex.strategy.entity.PlanStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanStrategyMapperDTO {
    private PlanStrategy planStrategy;
    private PlanStrategyDTO request;
    private Lvl1 lvl1;
    private Lvl2 lvl2;
    private Lvl3 lvl3;
    private Lvl4 lvl4;
    private List<Fineline> finelines;
    private List<Style> styles;
    private List<CustomerChoice> customerChoices;
    private Integer channel;
    private String sizeStr;
    private Set<PlanClusterStrategy> planClusterStrategies;
    private ClusterMetadata clusterMetadata ;
}
