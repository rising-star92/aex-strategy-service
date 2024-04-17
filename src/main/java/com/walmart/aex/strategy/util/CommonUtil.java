package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyCcFixture;
import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.entity.StrategyStyleFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommonUtil {

    public static final Long STRATEGY_ID = 2L;
    public static final Integer ZERO_INTEGER = 0;

    public static Integer zeroIfNull(Integer bonus) {
        return (bonus == null) ? ZERO_INTEGER : bonus;
    }

    public static BigDecimal zeroIfNull(BigDecimal bonus) {
        return (bonus == null) ? BigDecimal.ZERO : bonus;
    }

    public static String getInStoreYrWkDesc(WeatherCluster weatherCluster){
        return Optional.ofNullable(weatherCluster.getInStoreDate())
                .map(FiscalWeek::getFiscalWeekDesc)
                .orElse(null);
    }

    public static Integer getInStoreYrWk(WeatherCluster weatherCluster){
        return Optional.ofNullable(weatherCluster.getInStoreDate())
                .map(FiscalWeek::getWmYearWeek)
                .orElse(null);
    }

    public static FiscalWeek setInstoreMarkDownDate(String fiscalWeekDesc) {
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc(fiscalWeekDesc);
        return fiscalWeek;
    }

    public static String getMarkdownYrWkDesc(WeatherCluster weatherCluster){
        return Optional.ofNullable(weatherCluster.getMarkDownDate())
                .map(FiscalWeek::getFiscalWeekDesc)
                .orElse(null);
    }

    public static Integer getMarkdownYrWk(WeatherCluster weatherCluster){
        return Optional.ofNullable(weatherCluster.getMarkDownDate())
                .map(FiscalWeek::getWmYearWeek)
                .orElse(null);
    }

    public static Map<String, String> getUpdatedFieldsMap(List<Field> fields) {
        return Optional.of(fields).map(fieldsList -> fieldsList.stream().collect(Collectors.toMap(Field::getKey,
                Field::getValue))).orElse(null);
    }

    public static Integer getAnalyticsClusterId(WeatherCluster weatherCluster) {
        Integer analyticsClusterId = null;
        if (weatherCluster != null) {
            analyticsClusterId = weatherCluster.getType().getAnalyticsClusterId();
        }
        return analyticsClusterId;
    }
    public static WeatherCluster getWeatherClusterCc(CustomerChoice customerChoice) {
        return Optional.ofNullable(customerChoice)
                .map(CustomerChoice::getStrategy)
                .map(Strategy::getWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    public static Integer getFiscalYearFromWeekDesc(String fiscalWeekDesc){
        return Optional.ofNullable(fiscalWeekDesc)
                .map(inStoreYear -> Integer.valueOf(inStoreYear.substring(3,7)))
                .orElse(null);
    }

    public static Integer getFiscalWeekFromDesc(String fiscalWeekDesc){
        return Optional.ofNullable(fiscalWeekDesc)
                .map(markDownWeek -> Integer.valueOf(markDownWeek.substring(9,11)))
                .orElse(null);
    }

    public static WeatherCluster getWeatherClusterFineline(Fineline fineline) {
        return Optional.ofNullable(fineline)
                .map(Fineline::getStrategy)
                .map(Strategy::getWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    public static Integer getFinelineDetails(PlanStrategyRequest planStrategyRequest) {
        log.info("getFineline details from the planStrategyRequest ");
        return Optional.ofNullable(planStrategyRequest)
                .map(PlanStrategyRequest::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getFinelineNbr)
                .orElse(null);
    }

    public static Integer getFinelineDetailsFromListenerDTO(PlanStrategyDTO request) {
        log.info("getFineline details from the PlanStrategyDTO ");
        return Optional.ofNullable(request)
                .map(PlanStrategyDTO::getLvl1List)
                .stream()
                .flatMap(Collection::stream)
                .map(Lvl1::getLvl2List)
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl2::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getFinelineNbr)
                .orElse(null);
    }

    public static Integer getLvl3NbrFromListenerDTO(PlanStrategyDTO request) {
        log.info("get Lvl3Nbr details from the PlanStrategyDTO ");
        return Optional.ofNullable(request)
                .map(PlanStrategyDTO::getLvl1List)
                .stream()
                .flatMap(Collection::stream)
                .map(Lvl1::getLvl2List)
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl2::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl3Nbr)
                .orElse(null);
    }

    public static Integer getLvl4NbrFromListenerDTO(PlanStrategyDTO request) {
        log.info("get Lvl4Nbr details from the PlanStrategyDTO ");
        return Optional.ofNullable(request)
                .map(PlanStrategyDTO::getLvl1List)
                .stream()
                .flatMap(Collection::stream)
                .map(Lvl1::getLvl2List)
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl2::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getLvl4Nbr)
                .orElse(null);
    }

    public static Integer getLvl3Details(PlanStrategyRequest planStrategyRequest) {
        log.info("getLvl3 details from the planStrategyRequest ");
        return Optional.ofNullable(planStrategyRequest)
                .map(PlanStrategyRequest::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl3Nbr)
                .orElse(null);
    }

    public static Integer getLvl4Details(PlanStrategyRequest planStrategyRequest) {
        log.info("getLvl4 details from the planStrategyRequest ");
        return Optional.ofNullable(planStrategyRequest)
                .map(PlanStrategyRequest::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getLvl4Nbr)
                .orElse(null);
    }

    public static String getCcId(PlanStrategyRequest planStrategyRequest) {
        log.info("getCcId: {}", planStrategyRequest);
        return Optional.ofNullable(planStrategyRequest)
                .map(PlanStrategyRequest::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getStyles)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(CustomerChoice::getCcId)
                .orElse(null);
    }

    public static String getSeasonCode(String planDesc){
        return Optional.ofNullable(planDesc)
                .map(desc -> desc.substring(0,2))
                .orElse(null);
    }

    public static Integer getFiscalYearFromPlanDesc(String planDesc){
        return Optional.ofNullable(planDesc)
                .map(desc -> Integer.valueOf(desc.substring(planDesc.length()- 4)))
                .orElse(null);
    }


    public static String getAHSSeason(String planDesc){
        String[] seasonSplit = planDesc.split("-");
        return seasonSplit[0].trim() +
                "-" +
                "FYE" +
                seasonSplit[1].substring(2);

    }

    public static List<Integer> getRequestedLvl3List(PlanStrategyRequest request) {
        return Optional.ofNullable(request.getLvl3List())
                .stream().flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Lvl3::getLvl3Nbr).collect(Collectors.toList());
    }

    public static List<Integer> getRequestedLvl4List(PlanStrategyRequest request) {
        return Optional.ofNullable(request.getLvl3List())
                .stream().flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Lvl3::getLvl4List)
                .flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Lvl4::getLvl4Nbr)
                .collect(Collectors.toList());
    }

    public static List<Integer> getRequestedFlList(PlanStrategyRequest request) {
        return Optional.ofNullable(request.getLvl3List())
                .stream().flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Lvl3::getLvl4List)
                .flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Lvl4::getFinelines)
                .flatMap(Collection::stream).collect(Collectors.toList()).stream()
                .map(Fineline::getFinelineNbr)
                .collect(Collectors.toList());

    }

    public static String getRequestedFlChannel(Lvl3 lvl3) {
        return Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getChannel)
                .orElse(null);

    }

    public static Fixture fetchRequestedFixtureType(Strategy strategy) {
        return Optional.ofNullable(strategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    public static PresentationUnit fetchReqPresentationUnitFixtureType(Strategy strategy) {
        return Optional.ofNullable(strategy)
                .map(Strategy::getPresentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    public static void rollDownMaxCcsToSubCatgFlAndCcs(StrategySubCatgFixture strategySubCatgFixture, Integer maxCcs) {
        log.info("Set MaxCcs RollDown to Sub Catg for lvl4Nbr: {}", strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr());
        strategySubCatgFixture.setMaxCcsPerFixture(maxCcs);
        Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFinelineFixture -> rollDownMaxCcsToFlAndCcs(strategyFinelineFixture, maxCcs));
    }

    public static void rollDownMaxCcsToFlAndCcs(StrategyFinelineFixture strategyFinelineFixture, Integer maxCcs) {
        log.info("Set MaxCcs RollDown to Fl for finelineNbr: {}", strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr());
        strategyFinelineFixture.setMaxCcsPerFixture(maxCcs);
        //RollDownToCCs
        Optional.ofNullable(strategyFinelineFixture.getStrategyStyleFixtures())
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyStyleFixture::getStrategyCcFixtures)
                .flatMap(Collection::stream)
                .forEach(strategyCcFixture -> rollDownMaxCcToCCs(strategyCcFixture, maxCcs));
    }

    public static void rollDownMaxCcToCCs(StrategyCcFixture strategyCcFixture, Integer maxCcs) {
        log.info("Set adjMaxCcs Percentage for ccId: {}", strategyCcFixture.getStrategyCcFixtureId().getCcId());
        if(maxCcs == null || maxCcs == 0){
            strategyCcFixture.setAdjMaxFixturesPerCc(BigDecimal.ZERO);
        }else {
            strategyCcFixture.setAdjMaxFixturesPerCc(BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(maxCcs), 2, RoundingMode.HALF_UP));
        }
    }

    public static Integer getPercentageValue(Integer total, Integer count) {
        return (CommonUtil.zeroIfNull(total).equals(CommonUtil.ZERO_INTEGER)) ? null :
                (CommonUtil.zeroIfNull(new BigDecimal(count)).divide(new BigDecimal(CommonUtil.zeroIfNull(total)), 4,
                        RoundingMode.HALF_UP)).multiply(new BigDecimal(100)).intValue();
    }

    public static List<PresentationUnit> getFixturePresentationUnit(PlanStrategyDTO request) {
        log.info("Fetching Presentation Units strategy for the planId :{}", request.getPlanId());
        return Optional.ofNullable(request.getLvl1List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl1::getLvl2List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl2::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getStrategy)
                .map(Strategy::getPresentationUnits)
                .orElse(null);

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

}
