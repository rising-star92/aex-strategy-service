package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.sizepack.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.FixtureTypeRollup;
import com.walmart.aex.strategy.enums.MerchMethod;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.FixtureAllocationStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SPMerchMethFixtureMapper {

    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;

    public SPMerchMethFixtureMapper(FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository) {
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
    }

    /***
     * This function will map Merch method Fixture details till Fineline
     * @param merchMethodResponse
     * @param response
     */
    public void mapMerchMethodFixture(MerchMethodResponse merchMethodResponse, SPMerchMethodFixtureResponse response) {
        if (response.getPlanId() == null)
            response.setPlanId(merchMethodResponse.getPlanId());
        if (response.getLvl0Nbr() == null)
            response.setLvl0Nbr(merchMethodResponse.getLvl0Nbr());
        if (response.getLvl0Desc() == null)
            response.setLvl0Desc(merchMethodResponse.getLvl0GenDesc1());
        if (response.getLvl1Nbr() == null)
            response.setLvl1Nbr(merchMethodResponse.getLvl1Nbr());
        if (response.getLvl1Desc() == null)
            response.setLvl1Desc(merchMethodResponse.getLvl1GenDesc1());
        if (response.getLvl2Nbr() == null)
            response.setLvl2Nbr(merchMethodResponse.getLvl2Nbr());
        if (response.getLvl2Desc() == null)
            response.setLvl2Desc(merchMethodResponse.getLvl2GenDesc1());

        response.setLvl3List(mapSpMerchMethodLvl3List(merchMethodResponse, response));
    }

    private List<SPMerchMethLvl3Response> mapSpMerchMethodLvl3List(MerchMethodResponse merchMethodResponse, SPMerchMethodFixtureResponse response) {
        List<SPMerchMethLvl3Response> lvl3s = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());
        lvl3s.stream()
                .filter(lvl3 -> merchMethodResponse.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> {
                            List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = Optional.ofNullable(lvl3.getFixtureTypes()).orElse(new ArrayList<>());
                            spMerchMethFixtureResponseList.stream()
                                    .filter(spMerchMethFixtureResponse -> merchMethodResponse.getFixtureTypeId().equals(spMerchMethFixtureResponse.getFixtureTypeRollupId())).findFirst()
                                    .ifPresentOrElse(spMerchMethFixtureResponse -> appendMerchMethod(spMerchMethFixtureResponse, merchMethodResponse),
                                            () -> spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse)));
                            lvl3.setLvl4List(mapSpMerchMethodLvl4(merchMethodResponse, lvl3));

                        },
                        () -> setLvl3SP(merchMethodResponse, lvl3s));
        return lvl3s;
    }

    private void setLvl3SP(MerchMethodResponse merchMethodResponse, List<SPMerchMethLvl3Response> lvl3s) {
        SPMerchMethLvl3Response lvl3 = new SPMerchMethLvl3Response();
        lvl3.setLvl3Nbr(merchMethodResponse.getLvl3Nbr());
        lvl3.setLvl3Desc(merchMethodResponse.getLvl3GenDesc1());
        List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = new ArrayList<>();
        spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse));
        lvl3.setFixtureTypes(spMerchMethFixtureResponseList);
        lvl3s.add(lvl3);
        lvl3.setLvl4List(mapSpMerchMethodLvl4(merchMethodResponse, lvl3));
    }

    private void appendMerchMethod(SPMerchMethFixtureResponse spMerchMethFixtureResponse, MerchMethodResponse merchMethodResponse) {
        Set<String> merchMethods = new HashSet<>();
        if (spMerchMethFixtureResponse.getMerchMethodDesc() != null) {

            merchMethods = Stream.of(spMerchMethFixtureResponse.getMerchMethodDesc().trim().split(",")).collect(Collectors.toSet());
        }
        if (merchMethodResponse.getMerchMethodCode() != null) {
            merchMethods.add(MerchMethod.getMerchMethodFromId(merchMethodResponse.getMerchMethodCode()));
        }
        spMerchMethFixtureResponse.setMerchMethodDesc(String.join(",", merchMethods));
    }

    private SPMerchMethFixtureResponse addMerchMethod(MerchMethodResponse merchMethodResponse) {
        SPMerchMethFixtureResponse spMerchMethFixtureResponse = new SPMerchMethFixtureResponse();
        spMerchMethFixtureResponse.setFixtureTypeRollupId(merchMethodResponse.getFixtureTypeId());
        spMerchMethFixtureResponse.setMerchMethodCode(merchMethodResponse.getMerchMethodCode());
        spMerchMethFixtureResponse.setMerchMethodDesc(MerchMethod.getMerchMethodFromId(merchMethodResponse.getMerchMethodCode()));
        spMerchMethFixtureResponse.setFixtureType(FixtureTypeRollup.getFixtureTypeFromId(merchMethodResponse.getFixtureTypeId()));
        return spMerchMethFixtureResponse;
    }

    private List<SPMerchMethLvl4Response> mapSpMerchMethodLvl4(MerchMethodResponse merchMethodResponse, SPMerchMethLvl3Response lvl3) {
        List<SPMerchMethLvl4Response> lvl4s = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());
        lvl4s.stream()
                .filter(lvl4 -> merchMethodResponse.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> {
                            List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = Optional.ofNullable(lvl4.getFixtureTypes()).orElse(new ArrayList<>());
                            spMerchMethFixtureResponseList.stream()
                                    .filter(spMerchMethFixtureResponse -> merchMethodResponse.getFixtureTypeId().equals(spMerchMethFixtureResponse.getFixtureTypeRollupId())).findFirst()
                                    .ifPresentOrElse(spMerchMethFixtureResponse -> appendMerchMethod(spMerchMethFixtureResponse, merchMethodResponse),
                                            () -> spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse)));
                            lvl4.setFinelines(mapSpMerchMethodFineline(merchMethodResponse, lvl4));

                        },
                        () -> setLvl4SP(merchMethodResponse, lvl4s));
        return lvl4s;
    }

    private void setLvl4SP(MerchMethodResponse merchMethodResponse, List<SPMerchMethLvl4Response> lvl4s) {
        SPMerchMethLvl4Response lvl4 = new SPMerchMethLvl4Response();
        lvl4.setLvl4Nbr(merchMethodResponse.getLvl4Nbr());
        lvl4.setLvl4Desc(merchMethodResponse.getLvl4GenDesc1());
        List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = new ArrayList<>();
        spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse));
        lvl4.setFixtureTypes(spMerchMethFixtureResponseList);
        lvl4s.add(lvl4);
        lvl4.setFinelines(mapSpMerchMethodFineline(merchMethodResponse, lvl4));
    }

    private List<SPMerchMethFinelineResponse> mapSpMerchMethodFineline(MerchMethodResponse merchMethodResponse, SPMerchMethLvl4Response lvl4) {
        List<SPMerchMethFinelineResponse> finelineList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());
        finelineList.stream()
                .filter(finelineResponse -> merchMethodResponse.getFinelineNbr().equals(finelineResponse.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineResponse -> {
                            List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = Optional.ofNullable(finelineResponse.getFixtureTypes()).orElse(new ArrayList<>());
                            spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse));
                        },
                        () -> setFinelineSP(merchMethodResponse, finelineList));
        return finelineList;
    }

    private void setFinelineSP(MerchMethodResponse merchMethodResponse, List<SPMerchMethFinelineResponse> finelineList) {
        SPMerchMethFinelineResponse spMerchMethFinelineResponse = new SPMerchMethFinelineResponse();
        spMerchMethFinelineResponse.setFinelineNbr(merchMethodResponse.getFinelineNbr());
        spMerchMethFinelineResponse.setFinelineDesc(merchMethodResponse.getFinelineDesc());
        spMerchMethFinelineResponse.setAltFinelineName(merchMethodResponse.getAltFinelineName());
        List<SPMerchMethFixtureResponse> spMerchMethFixtureResponseList = new ArrayList<>();
        spMerchMethFixtureResponseList.add(addMerchMethod(merchMethodResponse));
        spMerchMethFinelineResponse.setFixtureTypes(spMerchMethFixtureResponseList);
        finelineList.add(spMerchMethFinelineResponse);
    }

    /***
     * This function will update Merch Method
     * @param lvl3
     * @param planStrategyId
     * @return strategyMerchCatgFixtures
     */
    public List<StrategyMerchCatgFixture> updateFixtureMerchMethod(SPMerchMethLvl3 lvl3, PlanStrategyId planStrategyId) {
        log.info("Calling the StrategyMerchCatgFixture repository planId {} & strategyId: {} & lvl3Nbr: {}",
                planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr());
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = fixtureAllocationStrategyRepository.
                findStrategyMerchCatgFixtureByStrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyIdAndStrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3Nbr(
                        planStrategyId, lvl3.getLvl3Nbr())
                .orElseThrow(() -> new CustomException(String.format("Fixture Merch Method doesn't exists for the PlanId :%s, StrategyId: %s  & lvl3Nbr : %s provided",
                        planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr())));

        Optional.ofNullable(lvl3.getFixtureTypes())
                .ifPresentOrElse(fixtures -> updateCatgMerchMethod(fixtures, strategyMerchCatgFixtures), () -> {
                    if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
                        subCatgMerchMethod(lvl3.getLvl4List(), strategyMerchCatgFixtures);
                    }
                });
        return strategyMerchCatgFixtures;
    }

    /***
     * This function will update Merch Method at catg level
     * @param fixtures
     * @param strategyMerchCatgFixtures
     */
    private void updateCatgMerchMethod(List<SPMerchMethFixture> fixtures, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (SPMerchMethFixture fixture : fixtures) {
            log.info("Updating catg fixture: {}", fixture.getFixtureTypeRollupId());
            StrategyMerchCatgFixture strategyMerchCatgFixture = fetchMerchCatgFixture(strategyMerchCatgFixtures, fixture.getFixtureTypeRollupId());
            if (strategyMerchCatgFixture != null) {
                strategyMerchCatgFixture.setMerchMethodCode(fixture.getMerchMethodCode());
                Set<StrategySubCatgFixture> strategySubCatgFixtures = Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures()).orElse(new HashSet<>());
                strategySubCatgFixtures.forEach(strategySubCatgFixture -> {
                    strategySubCatgFixture.setMerchTypeCode(fixture.getMerchMethodCode());
                    Set<StrategyFinelineFixture> strategyFinelineFixtures = Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures()).orElse(new HashSet<>());
                    strategyFinelineFixtures.forEach(strategyFinelineFixture -> strategyFinelineFixture.setMerchMethodCode(fixture.getMerchMethodCode()));
                });
            }
        }
    }

    /***
     * This function will update Merch Method starting at sub catg level
     * @param lvl4s
     * @param strategyMerchCatgFixtures
     */
    private void subCatgMerchMethod(List<SPMerchMethLvl4> lvl4s, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (SPMerchMethLvl4 lvl4 : lvl4s) {
            log.info("Updating Subcatg level lvl4Nbr: {}", lvl4.getLvl4Nbr());
            Optional.ofNullable(lvl4.getFixtureTypes())
                    .ifPresentOrElse(fixtures -> updateSubCatgMerchMethod(lvl4, fixtures, strategyMerchCatgFixtures), () -> {
                        if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                            finelineMerchMethod(lvl4, strategyMerchCatgFixtures);
                        }
                    });
        }
    }

    /***
     * This function will update Merch Method at sub catg level
     * @param lvl4
     * @param fixtures
     * @param strategyMerchCatgFixtures
     */
    private void updateSubCatgMerchMethod(SPMerchMethLvl4 lvl4, List<SPMerchMethFixture> fixtures, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (SPMerchMethFixture fixture : fixtures) {
            log.info("Updating subcatg fixture: {}", fixture.getFixtureTypeRollupId());
            StrategySubCatgFixture strategySubCatgFixture = fetchStrategySubCatgFixture(strategyMerchCatgFixtures, fixture.getFixtureTypeRollupId(), lvl4.getLvl4Nbr());
            if (strategySubCatgFixture != null) {
                strategySubCatgFixture.setMerchTypeCode(fixture.getMerchMethodCode());
                Set<StrategyFinelineFixture> strategyFinelineFixtures = Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures()).orElse(new HashSet<>());
                strategyFinelineFixtures.forEach(strategyFinelineFixture -> strategyFinelineFixture.setMerchMethodCode(fixture.getMerchMethodCode()));
            }
        }
    }

    /***
     * This function will update Merch Method starting at Fineline level
     * @param lvl4
     * @param strategyMerchCatgFixtures
     */
    private void finelineMerchMethod(SPMerchMethLvl4 lvl4, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (SPMerchMethFineline fineline : lvl4.getFinelines()) {
            log.info("Updating Fineline level fineline: {}", fineline.getFinelineNbr());
            Optional.ofNullable(fineline.getFixtureTypes())
                    .ifPresent(fixtures -> updateFinelineMerchMethod(lvl4, fineline, fixtures, strategyMerchCatgFixtures));
        }
    }

    /***
     * This function will update Merch Method at fineline level
     * @param lvl4
     * @param fineline
     * @param fixtures
     * @param strategyMerchCatgFixtures
     */
    private void updateFinelineMerchMethod(SPMerchMethLvl4 lvl4, SPMerchMethFineline fineline, List<SPMerchMethFixture> fixtures, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (SPMerchMethFixture fixture : fixtures) {
            log.info("Updating fineline fixture: {}", fixture.getFixtureTypeRollupId());
            StrategyFinelineFixture strategyFinelineFixture = fetchStrategyFlFixture(strategyMerchCatgFixtures, fixture.getFixtureTypeRollupId(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr());
            if (strategyFinelineFixture != null) {
                strategyFinelineFixture.setMerchMethodCode(fixture.getMerchMethodCode());
            }
        }
    }

    private StrategyMerchCatgFixture fetchMerchCatgFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, Integer fixtureType) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeId().equals(fixtureType))
                .findFirst()
                .orElse(null);
    }

    private StrategySubCatgFixture fetchStrategySubCatgFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, Integer fixtureType, Integer lvl4Nbr) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeId().equals(fixtureType))
                .findFirst()
                .map(StrategyMerchCatgFixture::getStrategySubCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst().orElse(null);
    }

    private StrategyFinelineFixture fetchStrategyFlFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, Integer fixtureType, Integer lvl4Nbr, Integer finelineNbr) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeId().equals(fixtureType))
                .findFirst()
                .map(StrategyMerchCatgFixture::getStrategySubCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(StrategySubCatgFixture::getStrategyFinelineFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr().equals(finelineNbr))
                .findFirst().orElse(null);
    }
}
