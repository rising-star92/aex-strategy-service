package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.repository.StrategyClusterStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsClusterStoreCountService {

    private final StrategyClusterStoreRepository strategyClusterStoreRepository;

    public AnalyticsClusterStoreCountService(StrategyClusterStoreRepository strategyClusterStoreRepository){
        this.strategyClusterStoreRepository = strategyClusterStoreRepository;
    }

    @Cacheable(value = "analyticsClusterStoreCount")
    public List<AnalyticsClusterStoreDTO> getAnalyticsClusterStoreCount(Long strategyId) {
        log.info("******************** Inside getAnalyticsClusterStoreCount Cache ********************");
        return Optional.ofNullable(strategyClusterStoreRepository.getAnalyticsStateProvinceStoreCount(strategyId))
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
