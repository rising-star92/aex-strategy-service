package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.WeatherClusterStrategy;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.enums.Location;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.FiscalWeekRepository;
import com.walmart.aex.strategy.util.CommonMethods;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PlanStrategyClusterEligRankingMapper {

    private final FiscalWeekRepository fiscalWeekRepository;
    @ManagedConfiguration
    private AppProperties appProperties;

    @Autowired
    private CommonMethods commonMethods;

    public PlanStrategyClusterEligRankingMapper(FiscalWeekRepository fiscalWeekRepository) {
        this.fiscalWeekRepository = fiscalWeekRepository;
    }

    public void mapPlanStrategyLvl2(WeatherClusterStrategy weatherClusterStrategy, PlanStrategyResponse strategyDTO, Integer finelineCheck) {
        if (strategyDTO.getPlanId() == null) {
            strategyDTO.setPlanId(weatherClusterStrategy.getPlanId());
        }
        if (strategyDTO.getLvl0Nbr() == null)
            strategyDTO.setLvl0Nbr(weatherClusterStrategy.getLvl0Nbr());
        if (strategyDTO.getLvl1Nbr() == null)
            strategyDTO.setLvl1Nbr(weatherClusterStrategy.getLvl1Nbr());
        if (strategyDTO.getLvl2Nbr() == null)
            strategyDTO.setLvl2Nbr(weatherClusterStrategy.getLvl2Nbr());
        strategyDTO.setLvl3List(mapPlanStrategyLvl3(weatherClusterStrategy, strategyDTO, finelineCheck));
    }

    private List<Lvl3> mapPlanStrategyLvl3(WeatherClusterStrategy weatherClusterStrategy, PlanStrategyResponse strategyDTO, Integer finelineCheck) {
        List<Lvl3> lvl3List = Optional.ofNullable(strategyDTO.getLvl3List())
                .orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> weatherClusterStrategy.getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapPlanStrategyLvl4(weatherClusterStrategy, lvl3, finelineCheck)),
                        () -> setLvl3(weatherClusterStrategy, finelineCheck, lvl3List));
        return lvl3List;
    }

    private void setLvl3(WeatherClusterStrategy weatherClusterStrategy, Integer finelineCheck, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(weatherClusterStrategy.getLvl3Nbr());
        lvl3.setLvl3Name(weatherClusterStrategy.getLvl3Name());
        lvl3List.add(lvl3);
        lvl3.setLvl4List(mapPlanStrategyLvl4(weatherClusterStrategy, lvl3, finelineCheck));
    }

    private List<Lvl4> mapPlanStrategyLvl4(WeatherClusterStrategy weatherClusterStrategy, Lvl3 lvl3, Integer finelineCheck) {
        List<Lvl4> lvl4List = Optional.ofNullable(lvl3.getLvl4List())
                .orElse(new ArrayList<>());

        lvl4List.stream()
                .filter(lvl4 -> weatherClusterStrategy.getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapPlanStrategyFineline(weatherClusterStrategy, lvl4, finelineCheck)),
                        () -> setLvl4(weatherClusterStrategy, finelineCheck, lvl4List));
        return lvl4List;
    }

    private void setLvl4(WeatherClusterStrategy weatherClusterStrategy, Integer finelineCheck, List<Lvl4> lvl4List) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(weatherClusterStrategy.getLvl4Nbr());
        lvl4.setLvl4Name(weatherClusterStrategy.getLvl4Name());
        lvl4List.add(lvl4);
        lvl4.setFinelines(mapPlanStrategyFineline(weatherClusterStrategy, lvl4, finelineCheck));
    }

    private List<Fineline> mapPlanStrategyFineline(WeatherClusterStrategy weatherClusterStrategy, Lvl4 lvl4, Integer finelineCheck) {
        List<Fineline> finelineList = Optional.ofNullable(lvl4.getFinelines())
                .orElse(new ArrayList<>());

        finelineList.stream()
                .filter(fineline -> weatherClusterStrategy.getFinelineNbr().equals(fineline.getFinelineNbr()))
                .findFirst()
                .ifPresentOrElse(fineline -> setFinelineStrategy(weatherClusterStrategy, finelineCheck, fineline),
                        () -> setFineline(weatherClusterStrategy, finelineCheck, finelineList));
        return finelineList;
    }

    private void setFineline(WeatherClusterStrategy weatherClusterStrategy, Integer finelineCheck, List<Fineline> finelineList) {
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(weatherClusterStrategy.getFinelineNbr());
        fineline.setFinelineName(weatherClusterStrategy.getFinelineDesc());
        fineline.setAltFinelineName(weatherClusterStrategy.getAltFinelineName());
        finelineList.add(fineline);
        setFinelineStrategy(weatherClusterStrategy, finelineCheck, fineline);
    }

    private void setFinelineStrategy(WeatherClusterStrategy weatherClusterStrategy, Integer finelineCheck, Fineline fineline) {
        fineline.setOutFitting(weatherClusterStrategy.getOutFitting());

        //Setting the brands
        List<Brands> brands = weatherClusterStrategy.getBrands()!=null? commonMethods.getBrandAttributes(weatherClusterStrategy.getBrands()):
               new ArrayList<>();
        fineline.setBrands(brands);

        if (finelineCheck == null) {
            Strategy strategy = Optional.ofNullable(fineline.getStrategy())
                    .orElse(new Strategy());
            List<WeatherCluster> weatherClusterAttr = Optional.ofNullable(fineline.getStrategy())
                    .map(Strategy::getWeatherClusters)
                    .orElse(new ArrayList<>());
            fineline.setStrategy(mapStrategy(weatherClusterStrategy, weatherClusterAttr, strategy));
        } else {
            fineline.setStyles(mapPlanStrategyStyle(weatherClusterStrategy, fineline));
        }
    }

    private Strategy mapStrategy(WeatherClusterStrategy weatherClusterStrategy, List<WeatherCluster> weatherClusterAttr, Strategy strategy) {
        if (weatherClusterAttr.isEmpty()) {
            weatherClusterAttr.add(mapPlanStrategyWeatherClus(weatherClusterStrategy, new WeatherCluster()));
        } else {
            weatherClusterAttr.stream()
                    .filter(weatherCluster -> weatherClusterStrategy.getAnalyticsClusterId().equals(weatherCluster.getType().getAnalyticsClusterId()))
                    .findFirst()
                    .ifPresentOrElse(weatherCluster -> {
                        if (weatherClusterStrategy.getIncludeOffshore() != null) {
                            weatherCluster.getIncludeOffshore().add(Optional.ofNullable(weatherClusterStrategy.getIncludeOffshore())
                                    .map(IncludeOffshoreMkt::valueOf)
                                    .orElse(null));
                        }
                    }, () -> weatherClusterAttr.add(mapPlanStrategyWeatherClus(weatherClusterStrategy, new WeatherCluster())));
        }
        strategy.setWeatherClusters(weatherClusterAttr);
        return strategy;
    }

    private List<Style> mapPlanStrategyStyle(WeatherClusterStrategy weatherClusterStrategy, Fineline fineline) {
        List<Style> styleList = Optional.ofNullable(fineline.getStyles())
                .orElse(new ArrayList<>());

        styleList.stream()
                .filter(style -> weatherClusterStrategy.getStyleNbr().equals(style.getStyleNbr()))
                .findFirst()
                .ifPresentOrElse(style -> style.setCustomerChoices(mapPlanStrategyCc(weatherClusterStrategy, style)),
                        () -> setStyle(weatherClusterStrategy, styleList));
        return styleList;
    }

    private void setStyle(WeatherClusterStrategy weatherClusterStrategy, List<Style> styleList) {
        Style style = new Style();
        style.setStyleNbr(weatherClusterStrategy.getStyleNbr());
        styleList.add(style);
        style.setCustomerChoices(mapPlanStrategyCc(weatherClusterStrategy, style));
    }

    private List<CustomerChoice> mapPlanStrategyCc(WeatherClusterStrategy weatherClusterStrategy, Style style) {
        List<CustomerChoice> customerChoiceList = Optional.ofNullable(style.getCustomerChoices())
                .orElse(new ArrayList<>());
        customerChoiceList.stream()
                .filter(customerChoice -> weatherClusterStrategy.getCcId().equals(customerChoice.getCcId()))
                .findFirst()
                .ifPresentOrElse(customerChoice -> setCcStrategy(weatherClusterStrategy, customerChoice),
                        () -> setCc(weatherClusterStrategy, customerChoiceList));
        return customerChoiceList;
    }

    private void setCc(WeatherClusterStrategy weatherClusterStrategy, List<CustomerChoice> customerChoiceList) {
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId(weatherClusterStrategy.getCcId());
        customerChoice.setAltCcDesc(weatherClusterStrategy.getAltCcDesc());
        customerChoice.setColorName(weatherClusterStrategy.getColorName());
        setCcStrategy(weatherClusterStrategy, customerChoice);
        customerChoiceList.add(customerChoice);
    }

    private void setCcStrategy(WeatherClusterStrategy weatherClusterStrategy, CustomerChoice customerChoice) {
        Strategy strategy = Optional.ofNullable(customerChoice.getStrategy())
                .orElse(new Strategy());
        List<WeatherCluster> weatherClusterAttr = Optional.ofNullable(customerChoice.getStrategy())
                .map(Strategy::getWeatherClusters)
                .orElse(new ArrayList<>());
        customerChoice.setStrategy(mapStrategy(weatherClusterStrategy, weatherClusterAttr, strategy));
    }

    private WeatherCluster mapPlanStrategyWeatherClus(WeatherClusterStrategy weatherClusterStrategy, WeatherCluster weatherCluster) {
        if (weatherCluster.getType() == null || !weatherCluster.getType().getAnalyticsClusterId().equals(weatherClusterStrategy.getAnalyticsClusterId())) {
            ClusterType type = new ClusterType();
            type.setAnalyticsClusterId(weatherClusterStrategy.getAnalyticsClusterId());
            type.setAnalyticsClusterDesc(weatherClusterStrategy.getAnalyticsClusterDesc());
            weatherCluster.setType(type);

            List<IncludeOffshoreMkt> excludeOffShores = new ArrayList<>();
            if (weatherClusterStrategy.getIncludeOffshore() != null) {
                excludeOffShores.add(IncludeOffshoreMkt.valueOf(weatherClusterStrategy.getIncludeOffshore()));
            }
            weatherCluster.setIncludeOffshore(excludeOffShores);
            weatherCluster.setInStoreDate(setInstoreMarkDownDate(weatherClusterStrategy.getInStoreDate()));
            weatherCluster.setMarkDownDate(setInstoreMarkDownDate(weatherClusterStrategy.getMarkDownDate()));
            weatherCluster.setAllocationWeeks(getAllocationWeeks(weatherClusterStrategy.getInStoreDate(), weatherClusterStrategy.getMarkDownDate()));
            weatherCluster.setIsEligibleFlag(weatherClusterStrategy.getIsEligibleFlag());
            weatherCluster.setLySales(weatherClusterStrategy.getLySales());
            weatherCluster.setLyUnits(weatherClusterStrategy.getLyUnits());
            weatherCluster.setForecastedSales(weatherClusterStrategy.getForecastedSales());
            weatherCluster.setForecastedUnits(weatherClusterStrategy.getForecastedUnits());
            weatherCluster.setOnHandQty(weatherClusterStrategy.getOnHandQty());
            weatherCluster.setSalesToStockRatio(weatherClusterStrategy.getSalesToStockRatio());
            weatherCluster.setRanking(weatherClusterStrategy.getRanking());
            weatherCluster.setAlgoClusterRanking(weatherClusterStrategy.getAlgoClusterRanking());
            if (weatherClusterStrategy.getIsEligible() == 1) {
                weatherCluster.setStoreCount(weatherClusterStrategy.getStoreCount());
            }
            if (weatherClusterStrategy.getIsEligibleFlag() == 1 ||
                    weatherClusterStrategy.getIsEligibleFlag() == 2) {
                weatherCluster.setStoreCount(weatherClusterStrategy.getStoreCount());
            } else if (weatherClusterStrategy.getIsEligibleFlag() == 0) {
                weatherCluster.setStoreCount(0);
            }
            if (weatherClusterStrategy.getAnalyticsClusterDesc().equalsIgnoreCase("All")) {
                if (weatherClusterStrategy.getIsEligible() == 1) {
                    weatherCluster.setLocation(Location.ALL_STORES.getValue());
                } else
                    weatherCluster.setLocation(Location.LOCALIZED.getValue());
            }
            if (weatherClusterStrategy.getAnalyticsClusterDesc().equalsIgnoreCase("All")) {
                if (weatherClusterStrategy.getIsEligibleFlag() == 1) {
                    weatherCluster.setLocation(Location.ALL_STORES.getValue());
                } else if (weatherClusterStrategy.getIsEligibleFlag() == 2) {
                    weatherCluster.setLocation(Location.LOCALIZED.getValue());
                }
            }
        }
        return weatherCluster;
    }

    private FiscalWeek setInstoreMarkDownDate(String fiscalWeekDesc) {
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc(fiscalWeekDesc == null ? null : fiscalWeekDesc.trim());
        return fiscalWeek;
    }

    public Integer getAllocationWeeks(String inStoreDateDesc, String markDownDesc) {
        //TODO: Once we have Ids coming from CLP we need to change to ids instead of Desc
        //allocation weeks includes the markdown week as well
        Integer allocationWeeks = null;
        if (inStoreDateDesc != null && markDownDesc != null) {
            Integer inStoreYr = CommonUtil.getFiscalYearFromWeekDesc(inStoreDateDesc);
            Integer inStoreWk = CommonUtil.getFiscalWeekFromDesc(inStoreDateDesc);
            Integer markDownYr = CommonUtil.getFiscalYearFromWeekDesc(markDownDesc);
            Integer markDownWk = CommonUtil.getFiscalWeekFromDesc(markDownDesc);
            if (inStoreYr != null && inStoreYr.equals(markDownYr)) {
                allocationWeeks = (markDownWk - inStoreWk) + 1;
            } else if (markDownYr != null && inStoreYr != null && markDownYr > inStoreYr) {
                Integer maxWeeksInStoreYr = fiscalWeekRepository.getMaxWeeksForWmYear(inStoreYr);
                allocationWeeks = ((maxWeeksInStoreYr - inStoreWk) + markDownWk) + 1;
            }
        }
        return allocationWeeks;
    }

}
