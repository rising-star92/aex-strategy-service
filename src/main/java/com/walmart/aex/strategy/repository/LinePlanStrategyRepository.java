package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.LinePlanCount;

import java.util.List;

public interface LinePlanStrategyRepository {

    List<LinePlanCount> fetchCurrentLinePlanCount(Long planId, Integer channelId);

}
