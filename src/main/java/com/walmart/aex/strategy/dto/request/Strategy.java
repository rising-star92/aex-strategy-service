package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.LinePlanStrategy;
import com.walmart.aex.strategy.dto.SizeCluster;
import lombok.Data;

import java.util.List;

@Data
public class Strategy {
    private List<WeatherCluster> weatherClusters;
    private List<Fixture> fixture;
    private List<PresentationUnit> presentationUnits;
    private LinePlanStrategy linePlan;
    private List<SizeProfileDTO> sizeProfiles;
    private List<SizeCluster> storeSizeClusters;
    private List<SizeCluster> onlineSizeClusters;
}
