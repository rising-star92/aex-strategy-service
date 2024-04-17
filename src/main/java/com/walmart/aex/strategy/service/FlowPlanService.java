package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.Finelines;
import com.walmart.aex.strategy.dto.request.FPVolumeDeviationUserSelectionRequest;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.entity.converter.VdLevelCodeConverter;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.enums.VdLevelCode;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.*;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FlowPlanService{

    private final String SUCCESS_MESSAGE="Successfully Saved";
    @Autowired
    private StratergyFLVGRepository strategyFLVGRepository;
    @Autowired
    private StrategyGroupRepository strategyGroupRepository;

    @Autowired
    private StrategyFinelineRepository strategyFinelineRepository;
    @Autowired
    private VDLevelTextRepository vdLevelTextRepository;
    @Autowired
    private PlanStrategyRepository planStrategyRepository;
    @ManagedConfiguration
    private AppProperties appProperties;


    @Transactional
    public String saveVolumeDeviationUserSelection(FPVolumeDeviationUserSelectionRequest request) throws JsonProcessingException {
        if(!Objects.isNull(request.getPlanId())){
            Long strategyId = getStratergyId();
            if(strategyId==null) return null;
            Optional<VDLevelText> vdLevelText = getVdLevelText(request);
            if(vdLevelText==null) return null;
            Long planId = request.getPlanId();
            Integer fineLineNbr = request.getFinelineNbr();
            Integer lvl0Nbr = request.getLvl0Nbr();
            Integer lvl1Nbr = request.getLvl1Nbr();
            Integer lvl2Nbr = request.getLvl2Nbr();
            Integer lvl3Nbr = request.getLvl3Nbr();
            Integer lvl4Nbr = request.getLvl4Nbr();
            Integer vdLevelCode = vdLevelText.get().getVdLevelCode();
            PlanStrategyId planStrategyId = new PlanStrategyId(planId,strategyId);
            StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategyId,lvl0Nbr,lvl1Nbr,lvl2Nbr,lvl3Nbr);
            StrategySubCatgId strategySubCatgId = new StrategySubCatgId(strategyMerchCatgId,lvl4Nbr);
            StrategyFinelineId strategyFinelineId = new StrategyFinelineId(strategySubCatgId,fineLineNbr);
            StratergyFLVG strategyFLVG = StratergyFLVG.builder().strategyFinelineId(strategyFinelineId)
                    .vdLevelCode(vdLevelCode)
                    .build();
            Optional<StratergyFLVG> flvgData = strategyFLVGRepository.findById(strategyFinelineId);
            if(flvgData.isEmpty()){
                log.info("Volume Deviation User Selection : Setting new Vd Level Code in DB");
                //set in plan_strategy first
                addPlanStratergy(planStrategyId);
                strategyFLVGRepository.save(strategyFLVG);
            }
            else{
                log.info("Volume Deviation User Selection : Data already exists in DB , Updating the Vd Level Code in ");
                flvgData.get().setVdLevelCode(vdLevelCode);
                strategyFLVGRepository.save(flvgData.get());
            }
        }
        return SUCCESS_MESSAGE;
    }

    @Transactional
    public String saveDefaultVolumeDeviation(List<Finelines> finelinesList) throws JsonProcessingException{
        List<StratergyFLVG> stratergyFLVGList = new ArrayList<>();
        VdLevelCodeConverter vdLevelCodeConverter = new VdLevelCodeConverter();
        Integer vdLevelCode = vdLevelCodeConverter.convertToDatabaseColumn(VdLevelCode.Fineline);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.VOLUME_DEVIATION_GROUPING.getStrategyGroupTypeCode(),
                Long.valueOf(finelinesList.get(0).getPlanId()));
        PlanStrategy planStrategy = new PlanStrategy();
        if(null == strategyId){
            Long stratId = strategyGroupRepository.getStrategyIdByStrategyGroupType(Long.valueOf(StratGroupType.VOLUME_DEVIATION_GROUPING.getStrategyGroupTypeCode()));
            planStrategy.setPlanStrategyId(new PlanStrategyId(Long.valueOf(finelinesList.get(0).getPlanId()),stratId));
            planStrategyRepository.save(planStrategy);
        }else {
            planStrategy.setPlanStrategyId(new PlanStrategyId(Long.valueOf(finelinesList.get(0).getPlanId()),strategyId));
        }
        finelinesList.stream().forEach(fineline -> {
            Long planId = Long.valueOf(fineline.getPlanId());
            Integer fineLineNbr = fineline.getFinelineId();
            Integer lvl0Nbr = fineline.getLvl0Nbr();
            Integer lvl1Nbr = fineline.getLvl1Nbr();
            Integer lvl2Nbr = fineline.getLvl2Nbr();
            Integer lvl3Nbr = fineline.getLvl3Nbr();
            Integer lvl4Nbr = fineline.getLvl4Nbr();
            StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategy.getPlanStrategyId(),lvl0Nbr,lvl1Nbr,lvl2Nbr,lvl3Nbr);
            StrategySubCatgId strategySubCatgId = new StrategySubCatgId(strategyMerchCatgId,lvl4Nbr);
            StrategyFinelineId strategyFinelineId = new StrategyFinelineId(strategySubCatgId,fineLineNbr);
            StratergyFLVG strategyFLVG = StratergyFLVG.builder().strategyFinelineId(strategyFinelineId)
                    .vdLevelCode(vdLevelCode)
                    .build();
            stratergyFLVGList.add(strategyFLVG);
        });
        strategyFLVGRepository.saveAll(stratergyFLVGList);
        return SUCCESS_MESSAGE;
    }

    private void addPlanStratergy(PlanStrategyId planStrategyId) {
        Optional<PlanStrategy> planStrategyFromDB=planStrategyRepository.findById(planStrategyId);
        if(planStrategyFromDB.isEmpty()){
            PlanStrategy planStrategy = PlanStrategy.builder().planStrategyId(planStrategyId).build();
            log.info("Volume Deviation User Selection : Adding entry into plan Startergy");
            planStrategyRepository.save(planStrategy);
        }
    }

    private Optional<VDLevelText> getVdLevelText(FPVolumeDeviationUserSelectionRequest request) {
        Optional<VDLevelText> vdLevelText = vdLevelTextRepository.findVdLevelCodeByVdLevelDesc(request.getVolumeDeviationLevel());
        if(vdLevelText.isEmpty()){
            log.error("Volume Deviation User Selection : data does not exists in VDLevelText DB ");
            return null;
        }
        return vdLevelText;
    }

    private Long getStratergyId() {
        Long strategyId = strategyGroupRepository.findStratergyId(appProperties.getVolumeDeviationAnalyticsClusterGroupDesc());
        if(strategyId==null || strategyId<=0){
            log.error("Volume Deviation User Selection : data does not exists in Stratergy Group DB");
            return null;
        }
        return strategyId;
    }



}

