package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdjustedSizeProfileUpdateMapper {

	private final ObjectMapper objectMapper;
	private final SizeAndPackService sizeAndPackService;

	@ManagedConfiguration
	private FeatureConfigProperties featureConfigProperties;

	public AdjustedSizeProfileUpdateMapper(ObjectMapper objectMapper, SizeAndPackService sizeAndPackService) {
		this.objectMapper = objectMapper;
		this.sizeAndPackService = sizeAndPackService;
	}
	
	public void updateFinelineAdjSizeProfile(List<StrategyFineLineSPCluster> finelineSPClusterList, Integer ahsSizeId, Integer clusterId, Double adjSizePct) {
		finelineSPClusterList.forEach(finelineSPCluster -> {
			if (clusterId == 0 || clusterId.equals(finelineSPCluster.getStrategyIFineLineId().getStrategySubCatgSPClusId()
					.getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId())) {
				List<SizeProfileDTO> profileDTOs = sizeAndPackService.safeReadSizeObject(finelineSPCluster.getSizeProfileObj());
				setAdjustedSizeProfile(profileDTOs, ahsSizeId, adjSizePct);
				finelineSPCluster.setSizeProfileObj(sizeProfileListToJsonStringMapper(profileDTOs));
			}
		});

		List<StrategyStyleSPCluster> styleSPClusterList = finelineSPClusterList.stream()
				.flatMap(finelinePClusObj -> finelinePClusObj.getStrategyStylesSPClusters().stream())
				.collect(Collectors.toList());

		updateStyleAdjSizeProfile(styleSPClusterList, ahsSizeId, clusterId, adjSizePct);
	}

	public void updateStyleAdjSizeProfile(List<StrategyStyleSPCluster> styleSPClusterList, Integer ahsSizeId, Integer clusterId, Double adjSizePct) {
		styleSPClusterList.forEach(styleSPCluster -> {
			if (clusterId == 0 || clusterId.equals(styleSPCluster.getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId()
					.getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId())) {
				List<SizeProfileDTO> profileDTOs = sizeAndPackService.safeReadSizeObject(styleSPCluster.getSizeProfileObj());
				setAdjustedSizeProfile(profileDTOs, ahsSizeId, adjSizePct);
				styleSPCluster.setSizeProfileObj(sizeProfileListToJsonStringMapper(profileDTOs));
			}
		});

		List<StrategyCcSPCluster> ccSPClusterList = styleSPClusterList.stream()
				.flatMap(styleSPClusObj -> styleSPClusObj.getStrategyCcSPClusters().stream())
				.collect(Collectors.toList());

		updateCustomerChoiceAdjSizeProfile(ccSPClusterList, ahsSizeId, clusterId, adjSizePct);
	}

	public void updateCustomerChoiceAdjSizeProfile(List<StrategyCcSPCluster> ccSPClusterList, Integer ahsSizeId, Integer clusterId, Double adjSizePct) {
		if (featureConfigProperties.getTotalSizePercentFeature()) {
			Set<Integer> eligibleSizeIds = sizeAndPackService.getEligibleSizeIds(getDefaultCcSPCluster(ccSPClusterList).getSizeProfileObj());
			ccSPClusterList.forEach(ccSPCluster -> {
				if (clusterId == 0 || clusterId.equals(ccSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId()
						.getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId())) {
					List<SizeProfileDTO> profileDTOs = sizeAndPackService.safeReadSizeObject(ccSPCluster.getSizeProfileObj());
					setAdjustedSizeProfile(profileDTOs, ahsSizeId, adjSizePct);
					String updatedSizeProfileObj = sizeProfileListToJsonStringMapper(profileDTOs);
					ccSPCluster.setSizeProfileObj(updatedSizeProfileObj);
					ccSPCluster.setTotalSizeProfilePct(sizeAndPackService.getTotalSizeProfilePct(updatedSizeProfileObj, eligibleSizeIds));
				}
			});
		} else {
			ccSPClusterList.forEach(ccSPCluster -> {
				if (clusterId == 0 || clusterId.equals(ccSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId()
						.getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId())) {
					List<SizeProfileDTO> profileDTOs = sizeAndPackService.safeReadSizeObject(ccSPCluster.getSizeProfileObj());
					setAdjustedSizeProfile(profileDTOs, ahsSizeId, adjSizePct);
					ccSPCluster.setSizeProfileObj(sizeProfileListToJsonStringMapper(profileDTOs));
				}
			});
		}
	}

	private StrategyCcSPCluster getDefaultCcSPCluster(List<StrategyCcSPCluster> ccSPClusterList) {
		return ccSPClusterList.stream()
				.filter(cc -> cc.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId()
						.getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0)).findFirst().orElse(new StrategyCcSPCluster());
	}

	private static void setAdjustedSizeProfile(List<SizeProfileDTO> profileDTOs, Integer ahsSizeId, Double adjSizePct) {
		profileDTOs.forEach(sizeProfileDto -> {
			if (sizeProfileDto.getAhsSizeId().equals(ahsSizeId)) {
				sizeProfileDto.setAdjustedSizeProfile(adjSizePct);
			}
		});
	}
	
	public String sizeProfileListToJsonStringMapper(List<SizeProfileDTO> profileDTOs)
	{
		String updatedSizeProfileObj = null;
		
		try
		{
			updatedSizeProfileObj = objectMapper.writeValueAsString(profileDTOs);
		}
		catch (JsonProcessingException e) {
			log.error("Error processing size profile", e);
		}
		
		return updatedSizeProfileObj;
		
	}
			

}
