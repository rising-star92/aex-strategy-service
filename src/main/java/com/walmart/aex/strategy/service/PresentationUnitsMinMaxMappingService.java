package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.entity.StrategyPUMinMax;
import com.walmart.aex.strategy.repository.StrategyPUMinMaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PresentationUnitsMinMaxMappingService {

    private final StrategyPUMinMaxRepository strategyPUMinMaxRepository;

    public PresentationUnitsMinMaxMappingService(StrategyPUMinMaxRepository strategyPUMinMaxRepository){
        this.strategyPUMinMaxRepository = strategyPUMinMaxRepository;
    }

    @Cacheable(value = "presentationUnitsMinMaxMapping")
    public List<StrategyPUMinMax> getPresentationUnitsMinMax() {
        log.info("******************** Inside getPresentationUnitsMinMax Cache ********************");
        return Optional.of(strategyPUMinMaxRepository.findAll())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
