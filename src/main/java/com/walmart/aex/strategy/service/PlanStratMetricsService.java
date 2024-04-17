package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.midas.FinelineRankMetricsDTO;
import com.walmart.aex.strategy.dto.midas.FinelineRankMetricsPayloadDTO;
import com.walmart.aex.strategy.dto.midas.ResultDTO;
import com.walmart.aex.strategy.dto.midas.StrongKeyFlat;
import com.walmart.aex.strategy.entity.PlanClusterStrategyId;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRankingId;
import com.walmart.aex.strategy.entity.StrategyFlClusMetrics;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.repository.StrategyFlClustMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlanStratMetricsService {

    @Autowired
    private MidasServiceCall midasServiceCall;
    @Autowired
    private StrategyFlClustMetricRepository strategyFlClustMetricRepository;
    @Autowired
    private StrategyFinelineRepository strategyFinelineRepository;

    public void fetchAndPersistMetricsData(StrongKeyFlat request, Long strategyId) {
        FinelineRankMetricsDTO finelineRankMetricsDTO = midasServiceCall.invokeMidasApi(request);
        ResultDTO resultDTO = Optional.ofNullable(finelineRankMetricsDTO.getPayload())
                .map(FinelineRankMetricsPayloadDTO::getResult)
                .orElse(new ResultDTO());

        PlanStrategyId planStrategyId = new PlanStrategyId(request.getPlanId(), strategyId);
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setLvl0Nbr(request.getLvl0Nbr());
        strategyFlClusEligRankingId.setLvl1Nbr(request.getLvl1Nbr());
        strategyFlClusEligRankingId.setLvl2Nbr(request.getLvl2Nbr());
        strategyFlClusEligRankingId.setLvl3Nbr(request.getLvl3Nbr());
        strategyFlClusEligRankingId.setLvl4Nbr(request.getLvl4Nbr());
        strategyFlClusEligRankingId.setFinelineNbr(request.getFinelineNbr());
        if (!CollectionUtils.isEmpty(resultDTO.getResponse())) {
            resultDTO.getResponse().stream().forEach(o -> {
                PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, o.getAnalyticsClusterId());
                strategyFlClusEligRankingId.setPlanClusterStrategyId(planClusterStrategyId);
                StrategyFlClusMetrics strategyFlClusMetrics = strategyFlClustMetricRepository.findByStrategyFlClusEligRankingId(strategyFlClusEligRankingId).orElse(null);
                if (strategyFlClusMetrics!=null) {
                    strategyFlClusMetrics.setSalesDollars(BigDecimal.valueOf(o.getSalesAmtLastYr()));
                    strategyFlClusMetrics.setSalesUnits(o.getSalesUnitsLastYr().intValue());
                    strategyFlClusMetrics.setForecastedUnits(o.getForecastedDemandUnits().intValue());
                    strategyFlClusMetrics.setForecastedDollars(BigDecimal.valueOf(o.getForecastedDemandSales()));
                    strategyFlClusMetrics.setOnHandQty(o.getOnHandQty().intValue());
                    strategyFlClusMetrics.setSalesToStockRatio(BigDecimal.valueOf(o.getSalesToStockRatio()));
                    strategyFlClusMetrics.setAnalyticsClusterRank(o.getRank());
                    strategyFlClustMetricRepository.save(strategyFlClusMetrics);
                }
            });
        }
    }

    public List<StrongKeyFlat> fetchFinelines(StrongKeyFlat request, Long strategyId) {
        List<StrongKeyFlat> result = strategyFinelineRepository.getFinelines(strategyId,
                request.getLvl0Nbr(), request.getLvl1Nbr(), request.getLvl2Nbr(), request.getLvl3Nbr(), request.getLvl4Nbr());
        result.stream().forEach(o -> {
            o.setSeasonCode(request.getSeasonCode());
            o.setFiscalYear(request.getFiscalYear());
        });
        return result;
    }
}
