package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.Rank.FlAndCcRankData;
import com.walmart.aex.strategy.dto.Rank.RankRequest;
import com.walmart.aex.strategy.dto.Rank.RankResponse;
import com.walmart.aex.strategy.repository.StrategyCcRepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankService {

    private final StrategyCcRepository strategyCcRepository;
    private final StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;
    private final StrategyFinelineRepository strategyFinelineRepository;
    private final StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    public RankService(StrategyCcRepository strategyCcRepository, StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository, StrategyFinelineRepository strategyFinelineRepository, StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository) {
        this.strategyCcRepository = strategyCcRepository;
        this.strategyFlClusEligRankingRepository = strategyFlClusEligRankingRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.strategyFlClusPrgmEligRankingRepository = strategyFlClusPrgmEligRankingRepository;
    }


    public RankResponse fetchFinelineRank(RankRequest request){
        RankResponse response = new RankResponse();
        //fetching rank data for all finelines
        List<FlAndCcRankData> flAndCcRankDataList = strategyFlClusEligRankingRepository.getFinelineRankData(
                isIncludeInFilter(request.getPlanId()), request.getPlanId(),
                isIncludeInFilter(request.getLvl0Nbr()), request.getLvl0Nbr(),
                isIncludeInFilter(request.getLvl1Nbr()), request.getLvl1Nbr(),
                isIncludeInFilter(request.getLvl2Nbr()), request.getLvl2Nbr(),
                isIncludeInFilter(request.getLvl3Nbr()), request.getLvl3Nbr(),
                isIncludeInFilter(request.getLvl4Nbr()), request.getLvl4Nbr(),
                request.getFinelineNbr(),request.getChannelId(),0);

        // fetching rank data for only traited finelines
        List<FlAndCcRankData> traitFlAndCcRankDataList = strategyFlClusPrgmEligRankingRepository.getFinelinePrgRank(
                isIncludeInFilter(request.getPlanId()), request.getPlanId(),
                isIncludeInFilter(request.getLvl0Nbr()), request.getLvl0Nbr(),
                isIncludeInFilter(request.getLvl1Nbr()), request.getLvl1Nbr(),
                isIncludeInFilter(request.getLvl2Nbr()), request.getLvl2Nbr(),
                isIncludeInFilter(request.getLvl3Nbr()), request.getLvl3Nbr(),
                isIncludeInFilter(request.getLvl4Nbr()), request.getLvl4Nbr(),
                request.getFinelineNbr(),request.getChannelId(),0);

        // comparing fineline ranks , if fineline has traited rank then we are setting that rank to the response list
        if(flAndCcRankDataList!=null && traitFlAndCcRankDataList != null)
        {
            flAndCcRankDataList.forEach(flAndCcRankData -> {
                traitFlAndCcRankDataList.forEach(flAndCcRankData1->{
                    if(flAndCcRankData.getPlanId().equals(flAndCcRankData1.getPlanId())
                            && flAndCcRankData.getLvl0Nbr().equals(flAndCcRankData1.getLvl0Nbr())
                            && flAndCcRankData.getLvl1Nbr().equals(flAndCcRankData1.getLvl1Nbr())
                            && flAndCcRankData.getLvl2Nbr().equals(flAndCcRankData1.getLvl2Nbr())
                            && flAndCcRankData.getLvl3Nbr().equals(flAndCcRankData1.getLvl3Nbr())
                            && flAndCcRankData.getLvl4Nbr().equals(flAndCcRankData1.getLvl4Nbr())
                            && flAndCcRankData.getFinelineNbr().equals(flAndCcRankData1.getFinelineNbr())
                            && flAndCcRankData1.getFlRank()!=null)
                    {
                        flAndCcRankData.setFlRank(flAndCcRankData1.getFlRank());
                    }
                });
            });
        }
        response.setFlAndCcRankData(flAndCcRankDataList);
        return response;
    }

    public RankResponse fetchCcRank(RankRequest request){
        RankResponse response = new RankResponse();
        //fetching rank data for all customerChoices
        List<FlAndCcRankData> flAndCcRankDataList = strategyCcRepository.getCcRankData(
                isIncludeInFilter(request.getPlanId()), request.getPlanId(),
                isIncludeInFilter(request.getLvl0Nbr()), request.getLvl0Nbr(),
                isIncludeInFilter(request.getLvl1Nbr()), request.getLvl1Nbr(),
                isIncludeInFilter(request.getLvl2Nbr()), request.getLvl2Nbr(),
                isIncludeInFilter(request.getLvl3Nbr()), request.getLvl3Nbr(),
                isIncludeInFilter(request.getLvl4Nbr()), request.getLvl4Nbr(),
                request.getFinelineNbr(),request.getStyleNbr(),request.getCcNo(),request.getChannelId(),0);

        // fetching rank data for only traited finelines and its customerChoices
        List<FlAndCcRankData> flAndCcRankPrgDataList = strategyCcRepository.getCcRankPrgData(
                isIncludeInFilter(request.getPlanId()), request.getPlanId(),
                isIncludeInFilter(request.getLvl0Nbr()), request.getLvl0Nbr(),
                isIncludeInFilter(request.getLvl1Nbr()), request.getLvl1Nbr(),
                isIncludeInFilter(request.getLvl2Nbr()), request.getLvl2Nbr(),
                isIncludeInFilter(request.getLvl3Nbr()), request.getLvl3Nbr(),
                isIncludeInFilter(request.getLvl4Nbr()), request.getLvl4Nbr(),
                request.getFinelineNbr(),request.getStyleNbr(),request.getCcNo(),request.getChannelId(),0);

        // comparing customerChoices ranks , if customerChoices belongs to traited fineline then we are setting that rank to the response list
        if(flAndCcRankDataList!=null && flAndCcRankPrgDataList != null)
        {
            flAndCcRankDataList.forEach(flAndCcRankData -> {
                flAndCcRankPrgDataList.forEach(flAndCcRankData1->{
                    if(flAndCcRankData.getPlanId().equals(flAndCcRankData1.getPlanId())
                            && flAndCcRankData.getLvl0Nbr().equals(flAndCcRankData1.getLvl0Nbr())
                            && flAndCcRankData.getLvl1Nbr().equals(flAndCcRankData1.getLvl1Nbr())
                            && flAndCcRankData.getLvl2Nbr().equals(flAndCcRankData1.getLvl2Nbr())
                            && flAndCcRankData.getLvl3Nbr().equals(flAndCcRankData1.getLvl3Nbr())
                            && flAndCcRankData.getLvl4Nbr().equals(flAndCcRankData1.getLvl4Nbr())
                            && flAndCcRankData.getFinelineNbr().equals(flAndCcRankData1.getFinelineNbr())
                            && flAndCcRankData.getStyleNbr().equals(flAndCcRankData1.getStyleNbr())
                            && flAndCcRankData.getCustomerChoice().equals(flAndCcRankData1.getCustomerChoice())
                            && flAndCcRankData1.getCcRank()!=null)
                    {
                        flAndCcRankData.setCcRank(flAndCcRankData1.getCcRank());
                    }
                });
            });
        }

        response.setFlAndCcRankData(flAndCcRankDataList);
        return response;
    }

    private Boolean isIncludeInFilter(List<?> lvlData) {
        return (lvlData == null || lvlData.size() == 0) ? null : true;
    }
}
