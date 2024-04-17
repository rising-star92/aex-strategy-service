package com.walmart.aex.strategy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.dto.SizeCluster;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.TraitChoiceType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class UpdateInStoreMarkDownUtil {
    private static Integer lvl0Nbr = 39107153;
    private static Integer lvl1Nbr = 50400;
    private static Integer lvl2Nbr = 105400;
    private static Integer lvl3Nbr = 34556;
    private static Integer lvl4Nbr = 4567;
    private static Integer fineline1Nbr = 151;
    private static String fineline1Desc = "Mens Top Hoodie";
    private static String style1Nbr = "151_23_01_001";
    private static String ccId = "151_23_01_001_001";
    private static String lvl0Desc ="General Merchandise";
    private static String lvl1Desc ="Apparel";
    private static String lvl2Desc ="Mens Apparel";
    private static String lvl3Desc ="Mens Tops";
    private static String lvl4Desc ="Mens Tops Active wear";

    public static <T> T convertStringToObject(String request, Class<T> classz) {
        ObjectMapper obj = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return obj.readValue(request, classz);
        } catch (JsonProcessingException e) {
            log.error("Error occured while parsing the JSON", e);
        }
        return null;
    }

    public static List<AnalyticsClusterStoreDTO> getAnalyticsTraitClusterStore(){
        List<AnalyticsClusterStoreDTO> analyticsClusterStoreDTOS = new ArrayList<>();
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO = new AnalyticsClusterStoreDTO(123L, 1, "PR", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO0 = new AnalyticsClusterStoreDTO(123L, 2, "HI", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO1 = new AnalyticsClusterStoreDTO(123L, 2, "TX", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO2 = new AnalyticsClusterStoreDTO(123L, 3, "VA", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO3 = new AnalyticsClusterStoreDTO(123L, 4, "NJ", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO4 = new AnalyticsClusterStoreDTO(123L, 5, "NY", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO5 = new AnalyticsClusterStoreDTO(123L, 6, "AR", 10L );
        AnalyticsClusterStoreDTO analyticsClusterStoreDTO6 = new AnalyticsClusterStoreDTO(123L, 7, "AK", 10L );
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO0);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO1);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO2);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO3);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO4);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO5);
        analyticsClusterStoreDTOS.add(analyticsClusterStoreDTO6);
        return analyticsClusterStoreDTOS;

    }

    public static Optional<List<WeatherCluster>> getWeatherClusterStrategy(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getWeatherClusterObj(new WeatherCluster(), i))
                .collect(Collectors.toList()));
    }

    private static WeatherCluster getWeatherClusterObj(WeatherCluster weatherCluster, int i) {
        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(i);
        if(i == 0){
            type.setAnalyticsClusterDesc("all");
        }else{
            type.setAnalyticsClusterDesc("cluster "+ i);
        }
        weatherCluster.setType(type);
        weatherCluster.setInStoreDisabledInLP(false);
        weatherCluster.setMarkDownDisabledInLP(false);
        FiscalWeek inStoreWeek = new FiscalWeek();
        inStoreWeek.setWmYearWeek(12301);
        inStoreWeek.setFiscalWeekDesc("FYE2023WK01");
        inStoreWeek.setWmYearWeek(202301);
        FiscalWeek markDownWeek = new FiscalWeek();
        markDownWeek.setWmYearWeek(12313);
        markDownWeek.setFiscalWeekDesc("FYE2023WK13");
        markDownWeek.setWmYearWeek(202313);
        weatherCluster.setInStoreDate(inStoreWeek);
        weatherCluster.setMarkDownDate(markDownWeek);
        weatherCluster.setSalesToStockRatio(BigDecimal.valueOf(10.09));
        weatherCluster.setForecastedSales(BigDecimal.valueOf(100000.00));
        weatherCluster.setForecastedUnits(50000);
        weatherCluster.setLySales(BigDecimal.valueOf(100000.00));
        weatherCluster.setLyUnits(40000);
        weatherCluster.setOnHandQty(400);
        return weatherCluster;
    }


    public static Optional<Set<StrategyFlClusEligRanking>> getStrategyFlClusEligRankings(boolean testCase10){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyFlClusEligRankingObj(new StrategyFlClusEligRanking(), i, testCase10))
                .collect(Collectors.toSet()));
    }

    public static Optional<Set<StrategyFlClusEligRanking>> getStrategyFlClusEligRankingsPartial(boolean testCase10){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyFlClusEligRankingObjPartial(new StrategyFlClusEligRanking(), i, testCase10))
                .collect(Collectors.toSet()));
    }

    private static StrategyFlClusEligRanking getStrategyFlClusEligRankingObj(StrategyFlClusEligRanking strategyFlClusEligRanking,
                                                                             int i, boolean testCase10){
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = getStrategyFlClusEligRankingId(i);
        strategyFlClusEligRanking.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);
        if(testCase10 && i == 0 || i == 1){
            strategyFlClusEligRanking.setIsEligibleFlag(0);
        } else {
            strategyFlClusEligRanking.setIsEligibleFlag(1);
        }
        strategyFlClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyFlClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyFlClusEligRanking.setMerchantOverrideRank(2);
        strategyFlClusEligRanking.setStrategyStyleCluses(Collections.singleton(getStrategyStyleClus(strategyFlClusEligRankingId)));
        strategyFlClusEligRanking.setStrategyFlMktClusEligs(getStrategyFlMktClusElig());
        return strategyFlClusEligRanking;
    }

    private static StrategyFlClusEligRanking getStrategyFlClusEligRankingObjPartial(StrategyFlClusEligRanking strategyFlClusEligRanking,
                                                                                    int i, boolean testCase10){
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = getStrategyFlClusEligRankingId(i);
        strategyFlClusEligRanking.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);
        if(testCase10 && i == 0 || i == 1){
            strategyFlClusEligRanking.setIsEligibleFlag(0);
        } else {
            strategyFlClusEligRanking.setIsEligibleFlag(1);
        }
        strategyFlClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyFlClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyFlClusEligRanking.setMerchantOverrideRank(2);
        strategyFlClusEligRanking.setStrategyStyleCluses(Collections.singleton(getStrategyStyleClusPartial(strategyFlClusEligRankingId)));
        strategyFlClusEligRanking.setStrategyFlMktClusEligs(getStrategyFlMktClusElig());
        return strategyFlClusEligRanking;
    }



    private static StrategyFlClusEligRankingId getStrategyFlClusEligRankingId(int i) {
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setPlanClusterStrategyId(getPlanClusterStrategyId(i));
        strategyFlClusEligRankingId.setFinelineNbr(fineline1Nbr);
        strategyFlClusEligRankingId.setLvl0Nbr(lvl0Nbr);
        strategyFlClusEligRankingId.setLvl1Nbr(lvl1Nbr);
        strategyFlClusEligRankingId.setLvl2Nbr(lvl2Nbr);
        strategyFlClusEligRankingId.setLvl3Nbr(lvl3Nbr);
        strategyFlClusEligRankingId.setLvl4Nbr(lvl4Nbr);
        return  strategyFlClusEligRankingId;
    }

    private static PlanClusterStrategyId getPlanClusterStrategyId(int i) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId();
        planClusterStrategyId.setPlanStrategyId(getPlanStrategyIdForCreate());
        planClusterStrategyId.setAnalyticsClusterId(i);
        return planClusterStrategyId;
    }

    public static PlanStrategyId getPlanStrategyId() {
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(1l);
        planStrategyId.setStrategyId(1l);
        return planStrategyId;
    }


    private static StrategyStyleClus getStrategyStyleClus(StrategyFlClusEligRankingId strategyFlClusEligRankingId) {
        StrategyStyleClus strategyStyleClus = new StrategyStyleClus();
        StrategyStyleClusId strategyStyleClusId = getStrategyStyleClusId(strategyFlClusEligRankingId);
        strategyStyleClus.setStrategyStyleClusId(strategyStyleClusId);
        strategyStyleClus.setStrategyCcClusEligRankings(Collections.singleton(getStrategyCcClus(strategyStyleClusId)));
        return strategyStyleClus;
    }

    private static StrategyStyleClus getStrategyStyleClusPartial(StrategyFlClusEligRankingId strategyFlClusEligRankingId) {
        StrategyStyleClus strategyStyleClus = new StrategyStyleClus();
        StrategyStyleClusId strategyStyleClusId = getStrategyStyleClusId(strategyFlClusEligRankingId);
        strategyStyleClus.setStrategyStyleClusId(strategyStyleClusId);
        strategyStyleClus.setStrategyCcClusEligRankings(Collections.singleton(getStrategyCcClusPartial(strategyStyleClusId)));
        return strategyStyleClus;
    }

    private static StrategyStyleClusId getStrategyStyleClusId(StrategyFlClusEligRankingId strategyFlClusEligRankingId) {
        StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId();
        strategyStyleClusId.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);
        strategyStyleClusId.setStyleNbr("151_23_01_001");
        return strategyStyleClusId;
    }


    private static StrategyCcClusEligRanking getStrategyCcClus(StrategyStyleClusId strategyStyleClusId) {
        StrategyCcClusEligRanking strategyCcClusEligRanking = new StrategyCcClusEligRanking();
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = getstrategyCcClusEligRankingId(strategyStyleClusId);
        strategyCcClusEligRanking.setIsEligibleFlag(1);
        strategyCcClusEligRanking.setStrategyCcClusEligRankingId(strategyCcClusEligRankingId);
        strategyCcClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyCcClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyCcClusEligRanking.setMerchantOverrideRank(1);
        return strategyCcClusEligRanking;

    }

    private static StrategyCcClusEligRanking getStrategyCcClusPartial(StrategyStyleClusId strategyStyleClusId) {
        StrategyCcClusEligRanking strategyCcClusEligRanking = new StrategyCcClusEligRanking();
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = getstrategyCcClusEligRankingId(strategyStyleClusId);
        strategyCcClusEligRanking.setIsEligibleFlag(1);
        strategyCcClusEligRanking.setStrategyCcClusEligRankingId(strategyCcClusEligRankingId);
        strategyCcClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyCcClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyCcClusEligRanking.setMerchantOverrideRank(1);
        return strategyCcClusEligRanking;

    }

    private static StrategyCcClusEligRankingId getstrategyCcClusEligRankingId(StrategyStyleClusId strategyStyleClusId) {
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = new StrategyCcClusEligRankingId();
        strategyCcClusEligRankingId.setStrategyStyleClusId(strategyStyleClusId);
        strategyCcClusEligRankingId.setCcId("151_23_01_001_001");
        return strategyCcClusEligRankingId;
    }

    private static Set<StrategyFlMktClusElig> getStrategyFlMktClusElig(){
        Set<StrategyFlMktClusElig> strategyFlMktClusEligs = new HashSet<>();
        StrategyFlMktClusElig strategyFlMktClusElig1 = new StrategyFlMktClusElig();
        StrategyFlMktClusElig strategyFlMktClusElig2 = new StrategyFlMktClusElig();
        StrategyFlMktClusEligId strategyFlMktClusEligId = new StrategyFlMktClusEligId();
        strategyFlMktClusEligId.setStrategyFlClusEligRankingId(getStrategyFlClusEligRankingId(1));
        strategyFlMktClusEligId.setMarketSelectCode(1);

        StrategyFlMktClusEligId strategyFlMktClusEligIdPR = new StrategyFlMktClusEligId();
        strategyFlMktClusEligId.setStrategyFlClusEligRankingId(getStrategyFlClusEligRankingId(1));
        strategyFlMktClusEligId.setMarketSelectCode(3);

        strategyFlMktClusElig1.setStrategyFlMktClusEligId(strategyFlMktClusEligId);
        strategyFlMktClusElig2.setStrategyFlMktClusEligId(strategyFlMktClusEligIdPR);
        strategyFlMktClusEligs.add(strategyFlMktClusElig1);
        strategyFlMktClusEligs.add(strategyFlMktClusElig2);
        return strategyFlMktClusEligs;
    }
    public static Optional<List<StrategyCcClusEligRanking>> getStrategyCcClusEligRanking(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyccClusEligRankingObj(new StrategyCcClusEligRanking(), i)).collect(Collectors.toList()));
    }

    public static Optional<List<StrategyCcClusEligRanking>> getStrategyCcClusEligRankingPartial(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyccClusEligRankingObjPartial(new StrategyCcClusEligRanking(), i)).collect(Collectors.toList()));
    }

    private static StrategyCcClusEligRanking getStrategyccClusEligRankingObj(StrategyCcClusEligRanking strategyCcClusEligRanking, int i) {
        StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId(getStrategyFlClusEligRankingId(i), "51_2_23_001");
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = new StrategyCcClusEligRankingId(strategyStyleClusId,"51_2_23_001_001");
        strategyCcClusEligRanking.setStrategyCcClusEligRankingId(strategyCcClusEligRankingId);
        strategyCcClusEligRanking.setIsEligibleFlag(1);
        strategyCcClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyCcClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyCcClusEligRanking.setMerchantOverrideRank(1);
        return strategyCcClusEligRanking;
    }

    private static StrategyCcClusEligRanking getStrategyccClusEligRankingObjPartial(StrategyCcClusEligRanking strategyCcClusEligRanking, int i) {
        StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId(getStrategyFlClusEligRankingId(i), "51_2_23_001");
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = new StrategyCcClusEligRankingId(strategyStyleClusId,"51_2_23_001_001");
        strategyCcClusEligRanking.setStrategyCcClusEligRankingId(strategyCcClusEligRankingId);
        strategyCcClusEligRanking.setIsEligibleFlag(1);
        strategyCcClusEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyCcClusEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyCcClusEligRanking.setMerchantOverrideRank(1);
        return strategyCcClusEligRanking;
    }

    public static PlanStrategyDTO getPlanStrategyRequest(){
        PlanStrategyDTO request = new PlanStrategyDTO();
        request.setPlanId(346l);
        request.setLvl0Nbr(lvl0Nbr);
        request.setLvl1List(getLvl1List());
        return request;
    }

    public static List<Lvl1> getLvl1List(){
        List<Lvl1> lvl1s = new ArrayList<>();
        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Nbr(lvl1Nbr);
        lvl1.setLvl1Name("Apparel");
        lvl1.setLvl2List(getLvl2List());
        lvl1s.add(lvl1);
        return lvl1s;
    }

    public static List<Lvl2> getLvl2List() {
        List<Lvl2> lvl2s = new ArrayList<>();
        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Nbr(lvl2Nbr);
        lvl2.setLvl2Name("Apparel plus");
        lvl2.setLvl3List(getLvl3List());
        lvl2s.add(lvl2);
        return lvl2s;
    }

    public static List<Lvl3> getLvl3List() {
        List<Lvl3> lvl3s = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(lvl3Nbr);
        lvl3.setLvl3Name("Women Apparel");
        Strategy strategy = new Strategy();
        List<SizeProfileDTO> sizeProfileCluster = UpdateInStoreMarkDownUtil.getSizeStrategy();
        strategy.setSizeProfiles(sizeProfileCluster);

        List<SizeCluster> storeSizeClusters = new ArrayList<>();
        SizeCluster sizeCluster = new SizeCluster();
        ClusterType clusterType = new ClusterType();
        clusterType.setAnalyticsClusterId(0);
        sizeCluster.setType(clusterType);
        sizeCluster.setSizeProfiles(sizeProfileCluster);
        storeSizeClusters.add(sizeCluster);

        strategy.setStoreSizeClusters(storeSizeClusters);

        lvl3.setStrategy(strategy);
        lvl3.setLvl4List(getLvl4List());
        lvl3s.add(lvl3);
        return  lvl3s;
    }


    public static List<SizeProfileDTO> getSizeStrategy(){
        List<SizeProfileDTO> sizeProfileStrategies = new ArrayList<>();
        SizeProfileDTO sizeProfile = GetSizeSPClusterObjUtil.getSizeProfileDTO();
        sizeProfileStrategies.add(sizeProfile);
        return sizeProfileStrategies;
    }

    public static List<Lvl4> getLvl4List() {
        List<Lvl4> lvl4s = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(lvl4Nbr);
        lvl4.setLvl4Name("Women Apparel Tops");
        lvl4.setFinelines(getFinelines());
        lvl4s.add(lvl4);
        return lvl4s;
    }

    public static List<Fineline> getFinelines() {
        List<Fineline> finelines = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(fineline1Nbr);
        fineline.setFinelineName("Tops Hoodie");
        fineline.setTraitChoice("Non Traited");
        fineline.setChannel("omni");
        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusters = UpdateInStoreMarkDownUtil.getWeatherClusterStrategy().orElse(null);
        strategy.setWeatherClusters(weatherClusters);
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();
        fixture.setType("Walls");
        fixture.setOrderPref(1);

        Fixture fixture1 = new Fixture();
        fixture1.setType("Tables");
        fixture1.setOrderPref(2);
        fixtures.add(fixture);
        fixtures.add(fixture1);
        strategy.setFixture(fixtures);

        //Presentation Units
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();
        presentationUnit.setType("Walls");
        PresentationUnit presentationUnit1 = new PresentationUnit();
        presentationUnit1.setType("Tables");
        presentationUnits.add(presentationUnit);
        presentationUnits.add(presentationUnit1);
        strategy.setPresentationUnits(presentationUnits);


        List<SizeCluster> storeSizeClusters = new ArrayList<>();
        SizeCluster sizeCluster = new SizeCluster();
        ClusterType clusterType = new ClusterType();
        clusterType.setAnalyticsClusterId(0);
        sizeCluster.setType(clusterType);
        //sizeCluster.setSizeProfileDTOS(sizeProfileCluster);
        storeSizeClusters.add(sizeCluster);

        strategy.setStoreSizeClusters(storeSizeClusters);


        fineline.setStrategy(strategy);
        fineline.setStyles(getStyle());
        finelines.add(fineline);
        return finelines;
    }

    public static List<Style> getStyle() {
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr("151_23_01_001");
        style.setChannel("omni");

        Strategy strategy = new Strategy();
        List<SizeCluster> storeSizeClusters = new ArrayList<>();
        SizeCluster sizeCluster = new SizeCluster();
        ClusterType clusterType = new ClusterType();
        clusterType.setAnalyticsClusterId(0);
        sizeCluster.setType(clusterType);
        //sizeCluster.setSizeProfileDTOS(sizeProfileCluster);
        storeSizeClusters.add(sizeCluster);

        strategy.setStoreSizeClusters(storeSizeClusters);

        style.setStrategy(strategy);

        style.setCustomerChoices(getCustomerChoice());
        styles.add(style);
        return styles;
    }

    public static List<CustomerChoice> getCustomerChoice() {
        List<CustomerChoice> customerChoices = new ArrayList<>();
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("151_23_01_001_001");
        customerChoice.setColorName("BlackSoot");
        customerChoice.setChannel("omni");
        Strategy ccStrategy = new Strategy();
        List<WeatherCluster> ccWeatherClusters = new ArrayList<>();
        WeatherCluster ccWeatherCluster = new WeatherCluster();
        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        ccWeatherCluster.setType(type);
        ccWeatherCluster.setInStoreDisabledInLP(false);
        ccWeatherCluster.setMarkDownDisabledInLP(false);
        FiscalWeek inStoreWeek = new FiscalWeek();
        inStoreWeek.setWmYearWeek(12301);
        inStoreWeek.setFiscalWeekDesc("FYE2023WK01");
        inStoreWeek.setWmYearWeek(202301);
        FiscalWeek markDownWeek = new FiscalWeek();
        markDownWeek.setWmYearWeek(12314);
        markDownWeek.setFiscalWeekDesc("FYE2023WK14");
        markDownWeek.setWmYearWeek(202314);
        ccWeatherCluster.setInStoreDate(inStoreWeek);
        ccWeatherCluster.setMarkDownDate(markDownWeek);
        ccWeatherClusters.add(ccWeatherCluster);
        ccStrategy.setWeatherClusters(ccWeatherClusters);


        List<SizeCluster> storeSizeClusters = new ArrayList<>();
        SizeCluster sizeCluster = new SizeCluster();
        ClusterType clusterType = new ClusterType();
        clusterType.setAnalyticsClusterId(0);
        sizeCluster.setType(clusterType);
        //sizeCluster.setSizeProfileDTOS(sizeProfileCluster);
        storeSizeClusters.add(sizeCluster);

        ccStrategy.setStoreSizeClusters(storeSizeClusters);

        customerChoice.setStrategy(ccStrategy);
        customerChoices.add(customerChoice);
        return customerChoices;
    }

    public static PlanStrategyId getPlanStrategyIdForCreate(){
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(346l);
        planStrategyId.setStrategyId(1l);
        return planStrategyId;
    }

    public static Fineline setFLBrand(Fineline fineLine){
        Brands brand = new Brands();
        brand.setBrandId(1);
        brand.setBrandName("Brand1");
        List<Brands> brands = new ArrayList<>();
        brands.add(brand);
        fineLine.setBrands(brands);
        return fineLine;
    }

    public static PlanStrategy getPlanStrategy(){
        PlanStrategy planStrategy = new PlanStrategy();
        planStrategy.setPlanStrategyId(getPlanStrategyIdForCreate());
        planStrategy.setStrategyMerchCatgs(getStrategyMerchCatg(getPlanStrategyIdForCreate()));
        planStrategy.setPlanClusterStrategies(getPlanStrategyCluster(getPlanStrategyIdForCreate()).orElse(null));
        return planStrategy;
    }

    private static Optional<Set<PlanClusterStrategy>> getPlanStrategyCluster(PlanStrategyId planStrategyId) {
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> setPlanStrategyClusterObj(new PlanClusterStrategy(), i, planStrategyId))
                .collect(Collectors.toSet()));
    }

    private static PlanClusterStrategy setPlanStrategyClusterObj(PlanClusterStrategy planClusterStrategy, int i, PlanStrategyId planStrategyId){
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, i);
        planClusterStrategy.setPlanClusterStrategyId(planClusterStrategyId);
        planClusterStrategy.setStrategyFlClusEligRankings(Collections.singleton(getStrategyFlClusEligRankingObj(new StrategyFlClusEligRanking(), i, false)));
        return planClusterStrategy;
    }

    private static PlanClusterStrategy setPlanStrategyClusterObjPartial(PlanClusterStrategy planClusterStrategy, int i, PlanStrategyId planStrategyId){
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, i);
        planClusterStrategy.setPlanClusterStrategyId(planClusterStrategyId);
        planClusterStrategy.setStrategyFlClusEligRankings(Collections.singleton(getStrategyFlClusEligRankingObjPartial(new StrategyFlClusEligRanking(), i, false)));
        return planClusterStrategy;
    }

    public static Set<StrategyMerchCatg> getStrategyMerchCatg(PlanStrategyId planStrategyId){
        Set<StrategyMerchCatg> strategyMerchCatgs = new HashSet<>();
        StrategyMerchCatg strategyMerchCatg = new StrategyMerchCatg();
        StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategyId, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr);
        strategyMerchCatg.setStrategyMerchCatgId(strategyMerchCatgId);
        strategyMerchCatg.setStrategySubCatgs(getStrategySubCatg(strategyMerchCatgId));
        strategyMerchCatgs.add(strategyMerchCatg);
        return strategyMerchCatgs;
    }

    private static Set<StrategySubCatg> getStrategySubCatg(StrategyMerchCatgId strategyMerchCatgId) {
        Set<StrategySubCatg> strategySubCatgs = new HashSet<>();
        StrategySubCatg strategySubCatg = new StrategySubCatg();
        StrategySubCatgId strategySubCatgId = new StrategySubCatgId(strategyMerchCatgId, lvl4Nbr);
        strategySubCatg.setStrategySubCatgId(strategySubCatgId);
        strategySubCatg.setStrategyFinelines(getStrategyFineline(strategySubCatgId));
        strategySubCatgs.add(strategySubCatg);
        return strategySubCatgs;
    }

    private static Set<StrategyFineline> getStrategyFineline(StrategySubCatgId strategySubCatgId) {
        Set<StrategyFineline> strategyFinelines = new HashSet<>();
        StrategyFineline strategyFineline = new StrategyFineline();
        StrategyFinelineId strategyFinelineId = new StrategyFinelineId(strategySubCatgId, fineline1Nbr);
        strategyFineline.setStrategyFinelineId(strategyFinelineId);
        strategyFineline.setChannelId(ChannelType.getChannelIdFromName("store"));
        strategyFineline.setTraitChoiceCode(TraitChoiceType.getTraitChoiceCodeFromName("Non-Traited"));
        strategyFineline.setFinelineDesc(fineline1Desc);
        strategyFineline.setLvl0GenDesc1(lvl0Desc);
        strategyFineline.setLvl1GenDesc1(lvl1Desc);
        strategyFineline.setLvl2GenDesc1(lvl2Desc);
        strategyFineline.setLvl3GenDesc1(lvl3Desc);
        strategyFineline.setLvl4GenDesc1(lvl4Desc);
        strategyFineline.setProductDimId(null);
        strategyFineline.setStrategyStyles(getStrategyStyle(strategyFinelineId));
        strategyFinelines.add(strategyFineline);
        return strategyFinelines;

    }

    private static Set<StrategyStyle> getStrategyStyle(StrategyFinelineId strategyFinelineId) {
        Set<StrategyStyle> strategyStyles = new HashSet<>();
        StrategyStyle strategyStyle = new StrategyStyle();
        StrategyStyleId strategyStyleId = new StrategyStyleId(strategyFinelineId, style1Nbr);
        strategyStyle.setStrategyStyleId(strategyStyleId);
        strategyStyle.setStrategyCcs(getStrategyCc(strategyStyleId));
        strategyStyles.add(strategyStyle);
        return strategyStyles;
    }

    private static Set<StrategyCc> getStrategyCc(StrategyStyleId strategyStyleId) {
        Set<StrategyCc> strategyCcs = new HashSet<>();
        StrategyCc strategyCc = new StrategyCc();
        StrategyCcId strategyCcId = new StrategyCcId(strategyStyleId, ccId);
        strategyCc.setStrategyCcId(strategyCcId);
        strategyCcs.add(strategyCc);
        return strategyCcs;
    }


}
