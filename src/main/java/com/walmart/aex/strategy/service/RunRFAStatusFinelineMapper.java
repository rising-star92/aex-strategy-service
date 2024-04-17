package com.walmart.aex.strategy.service;


import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.RFAStatusDataDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.AllocationRunTypeText;
import com.walmart.aex.strategy.util.CommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RunRFAStatusFinelineMapper {

    @Autowired
    private CommonMethods commonMethods;

    public void mapAllocationRuntype(AllocationRunTypeText allocationRunTypeText, List<RFAStatusDataDTO> allocationRunType){
        RFAStatusDataDTO dto = new RFAStatusDataDTO();
        dto.setCode(allocationRunTypeText.getAllocRunTypeCode());
        dto.setDescription(allocationRunTypeText.getAllocRunTypeDesc());
        allocationRunType.add(dto);
    }

    public void mapRunRFAStatusLvl2(FixtureAllocationStrategy runRFAStatusFineline, PlanStrategyResponse strategyDTO) {
        if (strategyDTO.getPlanId() == null) {
            strategyDTO.setPlanId(runRFAStatusFineline.getPlanId());
        }
        if (strategyDTO.getFixtureStrategyId() == null) {
            strategyDTO.setFixtureStrategyId(runRFAStatusFineline.getStrategyId());
        }
        if(strategyDTO.getWeatherClusterStrategyId() == null) {
            strategyDTO.setWeatherClusterStrategyId((runRFAStatusFineline.getWeatherClusterStrategyId()));
        }
        if(strategyDTO.getSeasonCode() == null){
            strategyDTO.setSeasonCode((runRFAStatusFineline.getSeasonCode()));
        }
        if(strategyDTO.getFiscalYear() == null){
            strategyDTO.setFiscalYear((runRFAStatusFineline.getFiscalYear()));
        }
        if (strategyDTO.getLvl0Nbr() == null)
            strategyDTO.setLvl0Nbr(runRFAStatusFineline.getLvl0Nbr());
        if (strategyDTO.getLvl1Nbr() == null)
            strategyDTO.setLvl1Nbr(runRFAStatusFineline.getLvl1Nbr());
        if (strategyDTO.getLvl2Nbr() == null)
            strategyDTO.setLvl2Nbr(runRFAStatusFineline.getLvl2Nbr());
        strategyDTO.setLvl3List(mapRunRFAStatusLvl3(runRFAStatusFineline, strategyDTO));
    }

    private List<Lvl3> mapRunRFAStatusLvl3(FixtureAllocationStrategy runRFAStatusFineline, PlanStrategyResponse strategyDTO) {
        List<Lvl3> lvl3List = Optional.ofNullable(strategyDTO.getLvl3List())
                .orElse(new ArrayList<>());
        lvl3List.stream()
                .filter(lvl3 -> runRFAStatusFineline.getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapRunRFAStatusLvl4(runRFAStatusFineline, lvl3)),
                        () -> setLvl3(runRFAStatusFineline, lvl3List));
        return lvl3List;
    }

    private void setLvl3(FixtureAllocationStrategy finelineRFALockStatus, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(finelineRFALockStatus.getLvl3Nbr());
        lvl3.setLvl3Name(finelineRFALockStatus.getLvl3Name());
        lvl3List.add(lvl3);
        lvl3.setLvl4List(mapRunRFAStatusLvl4(finelineRFALockStatus, lvl3));
    }

    private List<Lvl4> mapRunRFAStatusLvl4(FixtureAllocationStrategy runRFAStatusFineline, Lvl3 lvl3) {
        List<Lvl4> lvl4List = Optional.ofNullable(lvl3.getLvl4List())
                .orElse(new ArrayList<>());

        lvl4List.stream()
                .filter(lvl4 -> runRFAStatusFineline.getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapRunRFAStatusFineline(runRFAStatusFineline, lvl4)),
                        () -> setLvl4(runRFAStatusFineline, lvl4List));
        return lvl4List;
    }

    private void setLvl4(FixtureAllocationStrategy finelineRFALockStatus, List<Lvl4> lvl4List) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(finelineRFALockStatus.getLvl4Nbr());
        lvl4.setLvl4Name(finelineRFALockStatus.getLvl4Name());
        lvl4List.add(lvl4);
        lvl4.setFinelines(mapRunRFAStatusFineline(finelineRFALockStatus, lvl4));
    }

    private List<Fineline> mapRunRFAStatusFineline(FixtureAllocationStrategy runRFAStatusFineline, Lvl4 lvl4) {
        List<Fineline> finelineList = Optional.ofNullable(lvl4.getFinelines())
                .orElse(new ArrayList<>());
        finelineList.add(setFineline(runRFAStatusFineline ));
        return finelineList;
    }

    private Fineline setFineline(FixtureAllocationStrategy finelineRFALockStatus) {
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(finelineRFALockStatus.getFinelineNbr());
        fineline.setFinelineName(finelineRFALockStatus.getFinelineName());
        fineline.setAltFinelineName(finelineRFALockStatus.getAltFinelineName());
        fineline.setOutFitting(finelineRFALockStatus.getOutFitting());
        fineline.setTraitChoice(finelineRFALockStatus.getTraitChoice());
        fineline.setFinelineRank(finelineRFALockStatus.getFinelineRank());
        fineline.setAllocRunStatus(getRFAStatus(finelineRFALockStatus.getAllocRunTypeCode(), finelineRFALockStatus.getAllocRunTypeDesc()));
        fineline.setRunStatus(getRFAStatus(finelineRFALockStatus.getRunStatusCode(), finelineRFALockStatus.getRunStatusDesc()));
        fineline.setRfaStatus(getRFAStatus(finelineRFALockStatus.getRfaStatusCode(), finelineRFALockStatus.getRfaStatusDesc()));

        //Setting the brands
        List<Brands> brands = finelineRFALockStatus.getBrands()!=null? commonMethods.getBrandAttributes(finelineRFALockStatus.getBrands()):
                new ArrayList<>();
        fineline.setBrands(brands);
        return fineline;
    }

   private RFAStatusDataDTO getRFAStatus(Integer code, String description) {
        RFAStatusDataDTO rfaStatusData = new RFAStatusDataDTO();
       rfaStatusData.setCode(code);
       rfaStatusData.setDescription(description);
        return rfaStatusData;
   }

}
