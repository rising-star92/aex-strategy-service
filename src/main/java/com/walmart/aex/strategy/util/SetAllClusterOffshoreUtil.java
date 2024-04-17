package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SetAllClusterOffshoreUtil {


    public void setAllClusterOffshoreList(PlanStrategyResponse response) {
        Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .forEach(this::fetchLvl4List);
    }

    private void fetchLvl4List(Lvl3 lvl3) {
        Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .forEach(this::fetchFineline);
    }

    private void fetchFineline(Lvl4 lvl4) {
        Optional.ofNullable(lvl4.getFinelines())
                .stream()
                .flatMap(Collection::stream)
                .forEach(fineline -> {
                    if (fineline.getStrategy() != null) {
                        List<IncludeOffshoreMkt> includeOffshoreMkts = Optional.ofNullable(fineline.getStrategy())
                                .map(Strategy::getWeatherClusters)
                                .stream()
                                .flatMap(Collection::stream)
                                .map(WeatherCluster::getIncludeOffshore)
                                .flatMap(Collection::stream)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        Integer allClusterStoreCount = Optional.ofNullable(fineline.getStrategy())
                                .map(Strategy::getWeatherClusters)
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(weatherCluster -> !weatherCluster.getType().getAnalyticsClusterId().equals(0))
                                .mapToInt(s -> s.getStoreCount() != null ? s.getStoreCount() : 0)
                                .sum();

                            Optional.ofNullable(fineline.getStrategy())
                                    .map(Strategy::getWeatherClusters)
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .filter(weatherCluster -> CommonUtil.getAnalyticsClusterId(weatherCluster).equals(0))
                                    .findFirst()
                                    .ifPresent(weatherCluster -> {
                                        weatherCluster.setIncludeOffshore(includeOffshoreMkts);
                                        weatherCluster.setStoreCount(allClusterStoreCount);
                                    });
                        }
                    if (fineline.getStyles() != null) {
                        Optional.ofNullable(fineline.getStyles())
                                .stream()
                                .flatMap(Collection::stream)
                                .forEach(this::fetchCcs);
                    }
                });
    }

    private void fetchCcs(Style style) {
        Optional.ofNullable(style.getCustomerChoices())
                .stream()
                .flatMap(Collection::stream)
                .forEach(customerChoice -> {
                    if (customerChoice.getStrategy() != null) {
                        List<IncludeOffshoreMkt> includeOffshoreMkts = Optional.ofNullable(customerChoice.getStrategy())
                                .map(Strategy::getWeatherClusters)
                                .stream()
                                .flatMap(Collection::stream)
                                .map(WeatherCluster::getIncludeOffshore)
                                .flatMap(Collection::stream)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        if (!includeOffshoreMkts.isEmpty()) {
                            Optional.ofNullable(customerChoice.getStrategy())
                                    .map(Strategy::getWeatherClusters)
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .filter(weatherCluster -> CommonUtil.getAnalyticsClusterId(weatherCluster).equals(0))
                                    .findFirst()
                                    .ifPresent(weatherCluster -> weatherCluster.setIncludeOffshore(includeOffshoreMkts));
                        }
                    }
                });
    }

}
