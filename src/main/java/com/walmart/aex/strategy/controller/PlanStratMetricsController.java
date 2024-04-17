package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.midas.FinelineMetricsResponse;
import com.walmart.aex.strategy.dto.midas.GetFinelinesResponse;
import com.walmart.aex.strategy.dto.midas.StrongKeyFlat;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.service.PlanStratMetricsService;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class PlanStratMetricsController {

    public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    @ManagedConfiguration
    private AppProperties appProperties;
    private final PlanStratMetricsService planStratMetricsService;
    private final StrategyGroupRepository strategyGroupRepository;

    public PlanStratMetricsController(PlanStratMetricsService planStratMetricsService,
                                      StrategyGroupRepository strategyGroupRepository) {
        this.planStratMetricsService = planStratMetricsService;
        this.strategyGroupRepository = strategyGroupRepository;
    }

    @MutationMapping
    public FinelineMetricsResponse fetchAndUpdateFinelineMetrics(@Argument StrongKeyFlat request) {
        if (!Boolean.parseBoolean(appProperties.getAPS4ReleaseFlag())) return null;
        log.info("Fetching fineline metrics from Midas and persisting to strategy db for fineline: ", request.getFinelineNbr());
        FinelineMetricsResponse response = new FinelineMetricsResponse();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(),
                    request.getSeasonCode(), request.getFiscalYear());
            planStratMetricsService.fetchAndPersistMetricsData(request, strategyId);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching and updating fineline metrics data: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            response.setErrorMessage(exp.getMessage());
        }
        return response;
    }
    

    @QueryMapping
    public GetFinelinesResponse getFinelinesForSeasonCatg(@Argument StrongKeyFlat request) {
        if (!Boolean.parseBoolean(appProperties.getAPS4ReleaseFlag())) return null;
        log.info("Fetching Finelines for given season and category. Season: {}, Category: {}", request.getSeasonCode(),request.getLvl2Nbr());
        GetFinelinesResponse response = new GetFinelinesResponse();
        try {
            if (request.getStratGroupTypeId().equals(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode())) {
                Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(request.getStratGroupTypeId(),
                        request.getSeasonCode(), request.getFiscalYear());
                response.setPayload(planStratMetricsService.fetchFinelines(request, strategyId));
                response.setStatus(SUCCESS_STATUS);
            } else {
                response.setStatus(FAILURE_STATUS);
                response.setErrorMessage("No updates necessary. Analytics refresh not applicable to AP.");
            }
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Finelines for given season and category. Season: {}, " +
                    "Category: {}", request.getSeasonCode(),request.getLvl2Nbr());
            response.setStatus(FAILURE_STATUS);
            response.setErrorMessage(exp.getMessage());
        }
        return response;
    }

    @MutationMapping
    public Boolean analyticsTrigger(@Argument StrongKeyFlat request) {
        if (!Boolean.parseBoolean(appProperties.getAPS4ReleaseFlag())) return null;
        log.info("Fetching Fl Metrics from Midas Api and Inserting to Db for catg: ", request.getLvl2Nbr());
        try {
            GetFinelinesResponse response = getFinelinesForSeasonCatg(request);
            if (response.getStatus().equalsIgnoreCase(SUCCESS_STATUS) && !response.getPayload().isEmpty())
                response.getPayload().stream().forEach(o -> fetchAndUpdateFinelineMetrics(o));
            return Boolean.TRUE;
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating ClusterEligibility metrics: {}", exp.getMessage());
            return Boolean.FALSE;
        }
    }
}
