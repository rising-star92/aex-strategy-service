package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeatherCluster {
    private ClusterType type;
    private Boolean isEligible;
    private Integer isEligibleFlag;
    private String location;
    private List<Trait> trait;
    private Integer storeCount;
    private Integer allocationWeeks;
    private FiscalWeek inStoreDate;
    private FiscalWeek markDownDate;
    private Boolean inStoreDisabledInLP;
    private Boolean markDownDisabledInLP;
    private List<IncludeOffshoreMkt> includeOffshore;
    private BigDecimal lySales;
    private Integer lyUnits;
    private Integer onHandQty;
    private BigDecimal salesToStockRatio;
    private BigDecimal forecastedSales;
    private Integer forecastedUnits;
    private Integer ranking;
    private Integer algoClusterRanking;

}
