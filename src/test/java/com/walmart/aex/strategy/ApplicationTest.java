package com.walmart.aex.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.controller.*;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.PlanDTO.PlanDefMessageDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.dto.request.CustomerChoice;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.service.*;
import com.walmart.aex.strategy.util.FinelineRFALockingTestUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@AutoConfigureDataJpa
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ApplicationTest {
    static {
        System.setProperty("ccm.configs.dir", Paths.get("src", "test", "resources")
                .toFile().getAbsolutePath() + "/ccm");
    }

    @Autowired
    private PlanStrategyController planStrategyController;

    @Autowired
    private FixtureAllocationController fixtureAllocationController;

    @Autowired
    private LinePlanStrategyController linePlanStrategyController;

    @Autowired
    private FixtureAllocationService fixtureAllocationService;

    @Autowired
    private PresentationUnitController presentationUnitController;

    @MockBean
    private AppProperties appProperties;

    @Autowired
    private PlanStrategyService planStrategyService;

    @Autowired
    private ClusterEligibilityService clusterEligibilityService;

    @Autowired
    private PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper;

    @Autowired
    private RunRFAController runRFAController;

    @Autowired
    private RunRFAService runRFAService;

    public static <T> T convertStringToObject(String request, Class<T> classz) {
        ObjectMapper obj = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return obj.readValue(request, classz);
        } catch (JsonProcessingException e) {
            log.error("Error occured while parsing the JSON", e);
        }
        return null;
    }

    @SneakyThrows
    @BeforeEach
    public void setUp() throws IllegalAccessException {

        AppProperties appProperties1 = new AppProperties() {
            @Override
            public Integer weatherClusterStrategyId() {
                return null;
            }

            @Override
            public Integer fixtureStrategyId() {
                return null;
            }

            @Override
            public List<String> getCluster1OffshoreList() {
                return null;
            }

            @Override
            public List<String> getCluster2OffshoreList() {
                return null;
            }

            @Override
            public List<String> getCluster7OffshoreList() {
                return null;
            }

            @Override
            public String getStrategyGroupTypes() {
                return null;
            }

            @Override
            public String getAHSUrl() {
                return null;
            }

            @Override
            public String getAHSConsumerId() {
                return null;
            }

            @Override
            public String getAHSEnv() {
                return null;
            }

            @Override
            public List<String> getAHSAttributeTypes() {
                return null;
            }

            @Override
            public String getCSAUrl() {
                return null;
            }


            @Override
            public String getCSAEnv() {
                return null;
            }

            @Override
            public String getSPReleaseFlag() {
                return "true";
            }

            @Override
            public String getAPReleaseFlag() {
                return "true";
            }

            @Override
            public String getSPSpreadFeatureFlag() {
                return "true";
            }

            @Override
            public String getAPS1ReleaseFlag() {
                return "true";
            }

            @Override
            public String getAPS4ReleaseFlag() {
                return "false";
            }

            @Override
            public String getVolumeDeviationAnalyticsClusterGroupDesc() {
                return null;
            }

            @Override
            public List<String> getCsaSpaceType() {
                return null;
            }

            @Override
            public List<String> getCsaProgramsPlanId() { return new ArrayList<>(Arrays.asList("388", "387"));}

            @Override
            public String getPlanDefinitionUrl() {
                return null;
            }
        };
        ReflectionTestUtils.setField(planStrategyService, "appProperties", appProperties1);
        ReflectionTestUtils.setField(clusterEligibilityService, "appProperties", appProperties1);
        ReflectionTestUtils.setField(planStrategyClusterEligRankingMapper, "appProperties", appProperties1);



    }

    @Test
    void getPlanStrategiesCcByPlanFlTest() throws IOException {
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);

        finelineList.add(fineline);

        lvl4.setLvl4Nbr(234);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3.setLvl3Nbr(234);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);

        PlanStrategyResponse response = planStrategyController.getCcClusterEligRankingByPlanFl(planStrategyRequest);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void getPlanStrategiesByPlanTest() throws IOException {
        PlanStrategyResponse response = planStrategyController.getClusterEligRankingByPlan(1L);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void updateInvalidPlanStrategy() throws IOException {
        //Arrange
        String mockPlanStrategyResponse = "{\"planId\":123,\"planDesc\":\"S1 - FYE 2023\",\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1List\":[{\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2List\":[{\"lvl2Nbr\":1488,\"lvl2Desc\":\"Activewear Womens\",\"lvl3List\":[{\"lvl3Name\":\"Tops Activewear Womens\",\"lvl3Nbr\":9074,\"channel\":\"store\",\"lvl4List\":[{\"lvl4Name\":\"Jackets Tops Active Womens\",\"lvl4Nbr\":7204,\"channel\":\"store\",\"finelines\":[{\"finelineNbr\":123,\"finelineName\":\"Women Pullover hoodie\",\"channel\":\"store\",\"traitChoice\":\"TRAITED\",\"strategy\":{\"weatherClusters\":[{\"type\":{\"analyticsClusterId\":0,\"analyticsClusterDesc\":\"all\"},\"storeCount\":null,\"sellingWeeks\":null,\"inStoreDate\":{\"fiscalWeekDesc\":\"FYE2023WK01\",\"wmYearWeek\":12301,\"dwWeekId\":202402},\"markDownDate\":{\"fiscalWeekDesc\":\"FYE2023WK13\",\"wmYearWeek\":12313,\"dwWeekId\":202402},\"lySales\":null,\"lyUnits\":null,\"onHandQty\":null,\"salesToStockRatio\":null,\"forecastedSales\":null,\"forecastedUnits\":null,\"algoClusterRanking\":null}]}}]}]}]}]}]}";
        PlanStrategyDTO request = convertStringToObject(mockPlanStrategyResponse, PlanStrategyDTO.class);
        Exception exception = null;
        String actualMessage = null;
        //Act
        if (request != null) {
            exception = assertThrows(CustomException.class, () -> {
                planStrategyController.updatePlanStrategyForClpMetrics(request);
            });

        }
        String expectedMessage = "Invalid Plan Strategy Id";
        if (exception != null) {
            actualMessage = exception.getMessage();
        }
        //Assert
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testPlanCreate() throws IOException {

        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/planDefMessage.json")));
        PlanStrategyListenerResponse response = linePlanStrategyController.createPlanStrategy(new ObjectMapper().readValue(json, PlanDefMessageDTO.class));
        assertEquals("Success", response.getStatus());
    }

    @Test
    void testGetAllocationRulesByPlan() throws IOException {
        PlanStrategyResponse response = fixtureAllocationController.getAllocationRulesByPlan(1L);
        Long lvl3FixtureCount = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .map(Lvl3::getStrategy)
                .map(Strategy::getFixture)
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .count();
        assertEquals(3, lvl3FixtureCount);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void testGetAllocationRulesByCatgAndSubCatg() throws IOException {
        //Arrange
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);

        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3.setLvl3Nbr(9074);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        PlanStrategyResponse response = fixtureAllocationController.getAllocationRulesByCatgAndSubCatg(planStrategyRequest);
        Long finelineFixtureCount = Optional.ofNullable(response.getLvl3List())
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
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .count();
        assertEquals(3, finelineFixtureCount);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void testGetCcAllocationRulesByCatgSubCatgAndFl() throws IOException {
        //Arrange
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);

        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3.setLvl3Nbr(9074);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        PlanStrategyResponse response = fixtureAllocationController.getCcAllocationRulesByCatgSubCatgAndFl(planStrategyRequest);
        Long finelineFixtureCount = Optional.ofNullable(response.getLvl3List())
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
                .map(CustomerChoice::getStrategy)
                .map(Strategy::getFixture)
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .count();
        assertEquals(3, finelineFixtureCount);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void testUpdateFixtureAllocationAtCatgForMaxCcs(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        lvl3.setLvl3Nbr(9074);
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();

        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();
        fixtureField.setKey("maxCcs");
        fixtureField.setValue("4");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        lvl3.setUpdatedFields(updatedFields);

        fixture.setMaxCcs(3);
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        lvl3.setStrategy(strategy);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        Integer adjMaxCc = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getStrategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getMaxCcs)
                .orElse(null);

        BigDecimal adjMaxCcPer = Optional.ofNullable(response.getLvl3List())
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
                .map(CustomerChoice::getStrategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getAdjMaxCc)
                .orElse(null);

        //Assert
        assertEquals(3,adjMaxCc);
        assertEquals(BigDecimal.valueOf(33.33),adjMaxCcPer.stripTrailingZeros());
    }

    @Test
    void testUpdateFixtureAllocationAtCatgForDefaultMinCap(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        lvl3.setLvl3Nbr(9074);
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();

        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();
        fixtureField.setKey("defaultMinCap");
        fixtureField.setValue(".5");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        lvl3.setUpdatedFields(updatedFields);

        fixture.setDefaultMinCap(BigDecimal.valueOf(0.5));
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        lvl3.setStrategy(strategy);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        BigDecimal defaultMinCap = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getStrategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getDefaultMinCap)
                .orElse(null);
        //Assert
        assertEquals(BigDecimal.valueOf(0.5),defaultMinCap.stripTrailingZeros());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void testUpdateFixtureAllocationAtFlForBelowMin(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();
        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();

        lvl3.setLvl3Nbr(9074);
        fixtureField.setKey("belowMin");
        fixtureField.setValue("0.125");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        fixture.setBelowMin(BigDecimal.valueOf(0.125));
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        fineline.setFinelineNbr(465);
        fineline.setUpdatedFields(updatedFields);
        fineline.setStrategy(strategy);
        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        BigDecimal belowMin = Optional.ofNullable(response.getLvl3List())
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
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getBelowMin)
                .orElse(null);
        //Assert
        assertEquals(BigDecimal.valueOf(0.125),belowMin.stripTrailingZeros());
    }

    @Test
    void testUpdateFixtureAllocationAtFlForFgStart(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();
        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();

        lvl3.setLvl3Nbr(9074);
        fixtureField.setKey("fgStart");
        fixtureField.setValue("4");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        fixture.setFgStart(4);
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        fineline.setFinelineNbr(465);
        fineline.setUpdatedFields(updatedFields);
        fineline.setStrategy(strategy);
        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        Integer fgStart = Optional.ofNullable(response.getLvl3List())
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
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getFgStart)
                .orElse(null);
        //Assert
        assertEquals(4,fgStart);
    }

    @Test
    void testUpdateFixtureAllocationAtFlForMaxCcs(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();
        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();

        lvl3.setLvl3Nbr(9074);
        fixtureField.setKey("maxCcs");
        fixtureField.setValue("4");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        fixture.setMaxCcs(3);
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        fineline.setFinelineNbr(465);
        fineline.setUpdatedFields(updatedFields);
        fineline.setStrategy(strategy);
        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        Integer maxCcs = Optional.ofNullable(response.getLvl3List())
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
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getMaxCcs)
                .orElse(null);

        BigDecimal adjMaxCcPer = Optional.ofNullable(response.getLvl3List())
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
                .map(CustomerChoice::getStrategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getAdjMaxCc)
                .orElse(null);
        //Assert
        assertEquals(3,maxCcs);
        assertEquals(BigDecimal.valueOf(33.33),adjMaxCcPer.stripTrailingZeros());
    }

    @Test
    void testUpdateFixtureAllocationAtCcForAdjCcPercentage(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        Strategy strategy = new Strategy();
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture = new Fixture();
        UpdatedFields updatedFields = new UpdatedFields();
        Field fixtureField = new Field();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();

        List<Style> styleList = new ArrayList<>();
        Style style = new Style();
        List<CustomerChoice> customerChoiceList = new ArrayList<>();
        CustomerChoice customerChoice = new CustomerChoice();
        fixtureField.setKey("adjMaxCc");
        fixtureField.setValue("55.00");
        updatedFields.setFixture(Arrays.asList(fixtureField));
        fixture.setAdjMaxCc(BigDecimal.valueOf(55.55));
        fixture.setType("walls");
        fixtures.add(fixture);
        strategy.setFixture(fixtures);
        customerChoice.setCcId("test_cc");
        customerChoice.setUpdatedFields(updatedFields);
        customerChoice.setStrategy(strategy);
        customerChoiceList.add((customerChoice));

        style.setStyleNbr("test_style");
        style.setCustomerChoices(customerChoiceList);
        styleList.add(style);

        fineline.setFinelineNbr(465);
        fineline.setStyles(styleList);
        finelineList.add(fineline);
        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);
        lvl3.setLvl3Nbr(9074);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = fixtureAllocationController.updateFixtureAllocationMetrics(planStrategyRequest);

        BigDecimal adjMaxCcPer = Optional.ofNullable(response.getLvl3List())
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
                .map(CustomerChoice::getStrategy)
                .map(Strategy::getFixture)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture1 -> fixture1.getType().equals("walls"))
                .findFirst()
                .map(Fixture::getAdjMaxCc)
                .orElse(null);
        //Assert
        assertEquals(BigDecimal.valueOf(55.55),adjMaxCcPer.stripTrailingZeros());
    }

    @Test
    void testDeletePlanStrategyDeleteCc(){
        PlanStrategyDeleteMessage planStrategyDeleteMessage = new PlanStrategyDeleteMessage();
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(1l);
        strongKey.setPlanDesc("S2 - FYE 2023");
        strongKey.setLvl0Nbr(50000);
        strongKey.setLvl1Nbr(34);
        strongKey.setLvl2Nbr(1488);
        strongKey.setLvl3Nbr(9074);
        strongKey.setLvl4Nbr(7204);
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(4567);
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr("test_style_4567");
        List<CustomerChoice> customerChoices = new ArrayList<>();
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("test_cc_4567");
        customerChoices.add(customerChoice);
        style.setCustomerChoices(customerChoices);
        styles.add(style);
        fineline.setStyles(styles);
        strongKey.setFineline(fineline);
        planStrategyDeleteMessage.setStrongKey(strongKey);
        //Act
        PlanStrategyListenerResponse response = planStrategyController.deletePlanStrategy(planStrategyDeleteMessage);

        //Assert
        PlanStrategyResponse fetchResponse = new PlanStrategyResponse();
        PlanStrategyId planStrategyId = new PlanStrategyId(1l, 3l);
        Integer finelineNbr = 4567;
        fetchResponse = fixtureAllocationService.fetchUpdateFixtureChanges(planStrategyId,9074, 7204, null, null, fetchResponse);
        List<String> ccIds =
        Optional.ofNullable(fetchResponse.getLvl3List()).stream().flatMap(Collection::stream)
                .map(Lvl3::getLvl4List)
                .flatMap(Collection::stream)
                .map(Lvl4::getFinelines)
                .flatMap(Collection::stream)
                .filter(fineline1 -> fineline1.getFinelineNbr().equals(4567))
                .findFirst()
                .map(Fineline::getStyles)
                .stream()
                .flatMap(Collection::stream)
                .filter(style1 -> style1.getStyleNbr().equalsIgnoreCase("test_style_4567"))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .map(CustomerChoice::getCcId)
                .collect(Collectors.toList());
        assertEquals("Success", response.getStatus());
        assertEquals(1, ccIds.size());
        assertTrue(ccIds.contains("test_cc_4567_1"));

    }

    @Test
    void testDeletePlanStrategyDeleteStyle(){
        PlanStrategyDeleteMessage planStrategyDeleteMessage = new PlanStrategyDeleteMessage();
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(1l);
        strongKey.setPlanDesc("S2 - FYE 2023");
        strongKey.setLvl0Nbr(50000);
        strongKey.setLvl1Nbr(34);
        strongKey.setLvl2Nbr(1488);
        strongKey.setLvl3Nbr(9074);
        strongKey.setLvl4Nbr(7204);
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(4567);
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr("test_style_4567_1");
        styles.add(style);
        fineline.setStyles(styles);
        strongKey.setFineline(fineline);
        planStrategyDeleteMessage.setStrongKey(strongKey);
        //Act
        PlanStrategyListenerResponse response = planStrategyController.deletePlanStrategy(planStrategyDeleteMessage);

        //Assert
        PlanStrategyResponse fetchResponse = new PlanStrategyResponse();
        PlanStrategyId planStrategyId = new PlanStrategyId(1l, 3l);
        Integer finelineNbr = 4567;
        fetchResponse = fixtureAllocationService.fetchUpdateFixtureChanges(planStrategyId,9074, 7204, null, null, fetchResponse);
        Style afterStyle =
                Optional.ofNullable(fetchResponse.getLvl3List()).stream().flatMap(Collection::stream)
                        .map(Lvl3::getLvl4List)
                        .flatMap(Collection::stream)
                        .map(Lvl4::getFinelines)
                        .flatMap(Collection::stream)
                        .filter(fineline1 -> fineline1.getFinelineNbr().equals(4567))
                        .findFirst()
                        .map(Fineline::getStyles)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(style1 -> style1.getStyleNbr().equalsIgnoreCase("test_style_4567_1"))
                        .findFirst()
                        .orElse(null);
        assertEquals("Success", response.getStatus());
        assert afterStyle == null;

    }

    @Test
    void testDeletePlanStrategyDeleteFl(){
        PlanStrategyDeleteMessage planStrategyDeleteMessage = new PlanStrategyDeleteMessage();
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(1l);
        strongKey.setPlanDesc("S2 - FYE 2023");
        strongKey.setLvl0Nbr(50000);
        strongKey.setLvl1Nbr(34);
        strongKey.setLvl2Nbr(1488);
        strongKey.setLvl3Nbr(9074);
        strongKey.setLvl4Nbr(7204);
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(4567);
        strongKey.setFineline(fineline);
        planStrategyDeleteMessage.setStrongKey(strongKey);
        //Act
        PlanStrategyListenerResponse response = planStrategyController.deletePlanStrategy(planStrategyDeleteMessage);

        //Assert
        PlanStrategyResponse fetchResponse = new PlanStrategyResponse();
        PlanStrategyId planStrategyId = new PlanStrategyId(1l, 3l);
        Integer finelineNbr = 4567;
        fetchResponse = fixtureAllocationService.fetchUpdateFixtureChanges(planStrategyId,9074, 7204, null, null, fetchResponse);
        List<Fineline> finelines =
                Optional.ofNullable(fetchResponse.getLvl3List())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(Lvl3::getLvl4List)
                        .flatMap(Collection::stream)
                        .map(Lvl4::getFinelines)
                        .findAny()
                        .orElse(null);
        assertEquals("Success", response.getStatus());
        assertEquals(1, finelines.size());
        assertFalse(finelines.stream().anyMatch(fineline1 -> fineline1.getFinelineNbr().equals(4567)));

    }

    @Test
    void testGetPresentationUnitByPlan() throws IOException {
        PlanStrategyResponse response = presentationUnitController.getPresentationUnitByPlan(1L);
        Long lvl3FixtureCount = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .map(Lvl3::getStrategy)
                .map(Strategy::getPresentationUnits)
                .flatMap(Collection::stream)
                .map(PresentationUnit::getType)
                .count();
        assertEquals(3, lvl3FixtureCount);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }

    @Test
    void testGetPresentationUnitByCatgAndSubCatg() throws IOException {
        //Arrange
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();

        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);

        finelineList.add(fineline);

        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelineList);

        lvl4List.add(lvl4);

        lvl3.setLvl4List(lvl4List);
        lvl3.setLvl3Nbr(9074);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        PlanStrategyResponse response = presentationUnitController.getPresentationUnitByCatgAndSubCatg(planStrategyRequest);
        Long finelinePresentationUnitCount = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .map(PresentationUnit::getType)
                .count();
        assertEquals(3, finelinePresentationUnitCount);
        assertEquals(1l, response.getPlanId());
        assertEquals(1, response.getLvl3List().size());
    }


    @Test
    void testUpdatePresentationUnitAtCatgForMin(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        lvl3.setLvl3Nbr(9074);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();

        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("min");
        presentationUnitField.setValue("100");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        lvl3.setUpdatedFields(updatedFields);

        presentationUnit.setMin(120);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        lvl3.setStrategy(strategy);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer minValue = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getStrategy)
                .map(Strategy::getPresentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMin)
                .orElse(null);

        Integer minFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMin)
                .orElse(null);

        //Assert
        assertEquals(120,minValue);
        assertEquals(120,minFlvalue);
    }

    @Test
    void testUpdatePresentationUnitAtCatgForMax(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();

        lvl3.setLvl3Nbr(9074);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();

        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("max");
        presentationUnitField.setValue("200");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        lvl3.setUpdatedFields(updatedFields);

        presentationUnit.setMax(200);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        lvl3.setStrategy(strategy);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer maxValue = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getStrategy)
                .map(Strategy::getPresentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMax)
                .orElse(null);

        Integer maxFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMax)
                .orElse(null);

        //Assert
        assertEquals(200,maxValue);
        assertEquals(200, maxFlvalue);
    }

    @Test
    void testUpdatePresentationUnitAtSubCatgForMin(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(7204);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();

        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("min");
        presentationUnitField.setValue("200");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        lvl4.setUpdatedFields(updatedFields);
        presentationUnit.setMin(120);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        lvl4.setStrategy(strategy);
        lvl4List.add(lvl4);
        lvl3.setLvl3Nbr(9074);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer minValue = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getStrategy)
                .map(Strategy::getPresentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMin)
                .orElse(null);

        Integer minFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMin)
                .orElse(null);

        //Assert
        assertEquals(120,minValue);
        assertEquals(120, minFlvalue);
    }


    @Test
    void testUpdatePresentationUnitAtSubCatgForMax(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(7204);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();

        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("max");
        presentationUnitField.setValue("200");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        lvl4.setUpdatedFields(updatedFields);
        presentationUnit.setMax(200);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        lvl4.setStrategy(strategy);
        lvl4List.add(lvl4);
        lvl3.setLvl3Nbr(9074);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer maxValue = Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getStrategy)
                .map(Strategy::getPresentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMax)
                .orElse(null);

        Integer maxFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMax)
                .orElse(null);

        //Assert
        assertEquals(200,maxValue);
        assertEquals(200, maxFlvalue);
    }
    @Test
    void testUpdatePresentationUnitAtFlForMax(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        List<Fineline> finelines = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();
        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("max");
        presentationUnitField.setValue("200");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        fineline.setUpdatedFields(updatedFields);
        presentationUnit.setMax(200);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        fineline.setStrategy(strategy);
        finelines.add(fineline);
        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelines);
        lvl4List.add(lvl4);
        lvl3.setLvl3Nbr(9074);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer maxFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMax)
                .orElse(null);

        //Assert
        assertEquals(200, maxFlvalue);
    }

    @Test
    void testUpdatePresentationUnitAtFlForMin(){
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(1L);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        List<Fineline> finelines = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(465);
        Strategy strategy = new Strategy();
        List<PresentationUnit> presentationUnits = new ArrayList<>();
        PresentationUnit presentationUnit = new PresentationUnit();
        UpdatedFields updatedFields = new UpdatedFields();
        Field presentationUnitField = new Field();
        presentationUnitField.setKey("min");
        presentationUnitField.setValue("120");
        updatedFields.setPresentationUnits(Arrays.asList(presentationUnitField));
        fineline.setUpdatedFields(updatedFields);
        presentationUnit.setMin(120);
        presentationUnit.setType("walls");
        presentationUnits.add(presentationUnit);
        strategy.setPresentationUnits(presentationUnits);
        fineline.setStrategy(strategy);
        finelines.add(fineline);
        lvl4.setLvl4Nbr(7204);
        lvl4.setFinelines(finelines);
        lvl4List.add(lvl4);
        lvl3.setLvl3Nbr(9074);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);
        //Act
        PlanStrategyResponse response = presentationUnitController.updatePresentationUnitMetrics(planStrategyRequest);

        Integer minFlvalue = Optional.ofNullable(response.getLvl3List())
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
                .stream()
                .flatMap(Collection::stream)
                .filter(presentationUnit1 -> presentationUnit1.getType().equals("walls"))
                .findFirst()
                .map(PresentationUnit::getMin)
                .orElse(null);

        //Assert
        assertEquals(120, minFlvalue);
    }

    @Nested
    @DisplayName("Testing RFA status updates")
    class TestRfaStatusUpdate{
        String runStatusCode = "runStatusCode";
        String rfaStatusCode = "rfaStatusCode";
        String allocTypeCode = "allocTypeCode";
        @Test
        void testUpdateRFAAllocationTypeStatusForFL(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,399");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(allocTypeCode, 2);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, 7506, 16890, 399);

            //Assert
            assertNotEquals(null, fineline);
            assertNotEquals(null, fineline.getAllocRunStatus());
            assertEquals(2, fineline.getAllocRunStatus().getCode());
        }

        @Test
        void testUpdateRFARunningStatusForFL(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,399");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(rfaStatusCode, 2);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, 7506, 16890, 399);

            //Assert
            assertNotEquals(null, fineline);
            assertNotEquals(null, fineline.getRfaStatus());
            assertEquals(2, fineline.getRfaStatus().getCode());
        }
        @Test
        void testRFARunStatusForFLWhenRFAFailed(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,399");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(rfaStatusCode, 4);
            updatingFields.put(runStatusCode, 0);
            updatingFields.put(allocTypeCode, 1);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, 7506, 16890, 399);

            //Assert
            assertNotEquals(null, fineline);
            assertEquals(2, fineline.getRunStatus().getCode());
            assertEquals(1, fineline.getAllocRunStatus().getCode());
        }

        @Test
        void testRFARunStatusForFLWhenRFARunSuccessfull(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,473");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(rfaStatusCode, 3);
            updatingFields.put(runStatusCode, 0);
            updatingFields.put(allocTypeCode, 1);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, 7506, 16890, 473);

            //Assert
            assertNotEquals(null, fineline);
            assertEquals(3, fineline.getRfaStatus().getCode());
            assertEquals(0, fineline.getRunStatus().getCode());
            assertEquals(1, fineline.getAllocRunStatus().getCode());
        }

        @Test
        void testUpdateAllocTypeStatusForMultipleFLs(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,473");
            keys.add("8244,16891,190");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(allocTypeCode, 2);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            List<Fineline> finelines = new ArrayList<>();
            keys.forEach(key->{
                String[] uniqueKey = key.split(",", 3);
                Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, Integer.parseInt(uniqueKey[0]), Integer.parseInt(uniqueKey[1]), Integer.parseInt(uniqueKey[2]));
                finelines.add(fineline);
            });

            //Assert
            assertFalse(finelines.isEmpty());
            finelines.forEach(fineline1->assertEquals(2, fineline1.getAllocRunStatus().getCode()));
        }

        @Test
        void testUpdateRunStatusForMultipleFLs(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,473");
            keys.add("8244,16891,190");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(runStatusCode, 2);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            List<Fineline> finelines = new ArrayList<>();
            keys.forEach(key->{
                String[] uniqueKey = key.split(",", 3);
                Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, Integer.parseInt(uniqueKey[0]), Integer.parseInt(uniqueKey[1]), Integer.parseInt(uniqueKey[2]));
                finelines.add(fineline);
            });

            //Assert
            assertFalse(finelines.isEmpty());
            finelines.forEach(fineline1->assertEquals(2, fineline1.getRunStatus().getCode()));
        }

        @Test
        void testUpdateRfaStatusForMultipleFLs(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("7506,16890,473");
            keys.add("8244,16891,190");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(rfaStatusCode, 1);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L, keys, updatingFields, true);

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            List<Fineline> finelines = new ArrayList<>();
            keys.forEach(key->{
                String[] uniqueKey = key.split(",", 3);
                Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, Integer.parseInt(uniqueKey[0]), Integer.parseInt(uniqueKey[1]), Integer.parseInt(uniqueKey[2]));
                finelines.add(fineline);
            });

            //Assert
            assertFalse(finelines.isEmpty());
            finelines.forEach(fineline1->assertEquals(1, fineline1.getRfaStatus().getCode()));
        }

        @Test
        void testUpdateRfaStatusForMultipleFLsWithOneAllocatedAndOtherNotAllocated() {
            //Arrange
            List<String> keys1 = new ArrayList<>();
            keys1.add("8244,16893,151");
            HashMap<String, Integer> updatingFields1= new HashMap<>();
            updatingFields1.put(allocTypeCode, 1);
            updatingFields1.put(rfaStatusCode, 3);
            updatingFields1.put(runStatusCode, 0);

            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(3L,keys1, updatingFields1, false);

            HashMap<String, Integer> updatingFields2= new HashMap<>();
            updatingFields2.put(allocTypeCode, 1);
            updatingFields2.put(rfaStatusCode, 5);
            updatingFields2.put(runStatusCode, 0);

            UpdatedFields updatedFields = new UpdatedFields();
            List<Field> runRfaStatus = new ArrayList<>();
            updatingFields2.forEach((key, value)->{
                Field rfaStatus = new Field();
                rfaStatus.setKey(key);
                rfaStatus.setValue(value.toString());
                runRfaStatus.add(rfaStatus);
            });
            updatedFields.setRunRfaStatus(runRfaStatus);
            Fineline fineline = new Fineline();
            fineline.setFinelineNbr(232);
            fineline.setUpdatedFields(updatedFields);
            request.getLvl3List()
                    .forEach(lvl3 -> lvl3.getLvl4List().forEach(lvl4 -> lvl4.getFinelines().add(fineline)));

            //Act
            PlanStrategyResponse response = runRFAController.updateAndFetchFineLineRFAStatus(request);
            Fineline fineline151 = FinelineRFALockingTestUtil.getFineline(response, 8244, 16893, 151);
            Fineline fineline232 = FinelineRFALockingTestUtil.getFineline(response, 8244, 16893, 232);

            //Assert
            assertNotEquals(null, fineline151);
            assertEquals(3, fineline151.getRfaStatus().getCode());
            assertEquals(0, fineline151.getRunStatus().getCode());
            assertEquals(1, fineline151.getAllocRunStatus().getCode());

            assertNotEquals(null, fineline232);
            assertEquals(5, fineline232.getRfaStatus().getCode());
            assertEquals(2, fineline232.getRunStatus().getCode());
            assertEquals(1, fineline232.getAllocRunStatus().getCode());
        }

        @Test
        void testUpdateRfaStatusForMultipleFLsAfterRFAFailed(){
            //Arrange
            List<String> keys = new ArrayList<>();
            keys.add("9073,7203,123");
            HashMap<String, Integer> updatingFields= new HashMap<>();
            updatingFields.put(rfaStatusCode, 4);
            updatingFields.put(runStatusCode, 0);
            updatingFields.put(allocTypeCode, 1);
            PlanStrategyRequest request = FinelineRFALockingTestUtil.createRequest(2L,keys, updatingFields, true);

            //Act
            runRFAController.updateRFAStatusFromRfa(request);
            PlanStrategyResponse response = runRFAController.getRunRFAStatusByPlan(2L);
            Fineline fineline = FinelineRFALockingTestUtil.getFineline(response, 9073, 7203, 123);

            //Assert
            assertNotEquals(null, fineline);
            assertEquals(4, fineline.getRfaStatus().getCode());
            assertEquals(0, fineline.getRunStatus().getCode());
            assertEquals(1, fineline.getAllocRunStatus().getCode());
        }
    }
}
