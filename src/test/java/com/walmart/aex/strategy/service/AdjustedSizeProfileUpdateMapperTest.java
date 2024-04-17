package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.entity.*;

import static org.junit.jupiter.api.Assertions.*;

import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class AdjustedSizeProfileUpdateMapperTest {

	private static final Integer ahsSizeId = 246;
	private static final Integer clusterId = 0;
	private static final Double adjSizePct = 57.0;

	@InjectMocks
	AdjustedSizeProfileUpdateMapper adjustedSizeProfileUpdateMapper;

	List<StrategyFineLineSPCluster> finelineSPClusterList;

	Set<StrategyStyleSPCluster> styleSPClusterList;

	Set<StrategyCcSPCluster> ccSPClusterList;

	ObjectMapper objectMapper;

	@Mock
	SizeAndPackService sizeAndPackService;

	@Mock
	FeatureConfigProperties featureConfigProperties;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
		adjustedSizeProfileUpdateMapper = new AdjustedSizeProfileUpdateMapper(objectMapper, sizeAndPackService);
		ReflectionTestUtils.setField(adjustedSizeProfileUpdateMapper, "featureConfigProperties", featureConfigProperties);
		when(featureConfigProperties.getTotalSizePercentFeature()).thenReturn(true);
	}

	@Test
	void testUpdateFinelineAdjSizeProfile() throws JsonProcessingException {

		finelineSPClusterList = new ArrayList<>();
		StrategyStyleSPCluster strategyStyleSPCluster = new StrategyStyleSPCluster();
		StrategyCcSPCluster ccSPCluster = new StrategyCcSPCluster();
		StrategyFineLineSPCluster fineline = new StrategyFineLineSPCluster();
		fineline.setStrategyIFineLineId(getStrategyFinelineId(clusterId));
		fineline.setSizeProfileObj(getSizeProfileDTO().toString());
		finelineSPClusterList.add(fineline);

		styleSPClusterList = new HashSet<>();
		strategyStyleSPCluster.setStrategyStyleSPClusId(StrategyStyleSPClusId.builder().strategyFinelineSPClusId(fineline.getStrategyIFineLineId()).build());
		strategyStyleSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		styleSPClusterList.add(strategyStyleSPCluster);
		fineline.setStrategyStylesSPClusters(styleSPClusterList);

		ccSPClusterList = new HashSet<>();
		ccSPCluster.setStrategyCcSPClusId(StrategyCcSPClusId.builder().strategyStyleSPClusId(strategyStyleSPCluster.getStrategyStyleSPClusId()).build());
		ccSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		strategyStyleSPCluster.setStrategyCcSPClusters(ccSPClusterList);
		ccSPClusterList.add(ccSPCluster);

		List<SizeProfileDTO> updated = getSizeProfileDTO();
		updated.get(0).setAdjustedSizeProfile(adjSizePct);

		when(sizeAndPackService.getEligibleSizeIds(anyString())).thenReturn(Set.of(ahsSizeId));
		when(sizeAndPackService.safeReadSizeObject(anyString())).thenReturn(getSizeProfileDTO());
		when(sizeAndPackService.getTotalSizeProfilePct(objectMapper.writeValueAsString(updated), Set.of(ahsSizeId))).thenReturn(adjSizePct);

		adjustedSizeProfileUpdateMapper.updateFinelineAdjSizeProfile(finelineSPClusterList, ahsSizeId, clusterId,
				adjSizePct);

		List<SizeProfileDTO> finelineSizeProfiles = Arrays.asList(
				objectMapper.readValue(finelineSPClusterList.get(0).getSizeProfileObj(), SizeProfileDTO[].class));
		List<SizeProfileDTO> styleSizeProfiles = Arrays.asList(
				objectMapper.readValue(finelineSPClusterList.get(0)
						.getStrategyStylesSPClusters().iterator().next().getSizeProfileObj(), SizeProfileDTO[].class));
		StrategyCcSPCluster strategyCcSPCluster = finelineSPClusterList.get(0).getStrategyStylesSPClusters().iterator().next().getStrategyCcSPClusters().iterator().next();
		List<SizeProfileDTO> ccSizeProfiles = Arrays.asList(
				objectMapper.readValue(strategyCcSPCluster.getSizeProfileObj(), SizeProfileDTO[].class));

		assertEquals(adjSizePct, finelineSizeProfiles.get(0).getAdjustedSizeProfile());
		assertEquals(adjSizePct, styleSizeProfiles.get(0).getAdjustedSizeProfile());
		assertEquals(adjSizePct, ccSizeProfiles.get(0).getAdjustedSizeProfile());
		assertEquals(adjSizePct, strategyCcSPCluster.getTotalSizeProfilePct());
	}

	@Test
	void testUpdateFinelineAdjSizeProfileWithCluster() throws JsonProcessingException {

		finelineSPClusterList = new ArrayList<>();
		StrategyStyleSPCluster strategyStyleSPCluster = new StrategyStyleSPCluster();
		StrategyCcSPCluster ccSPCluster = new StrategyCcSPCluster();
		StrategyFineLineSPCluster fineline = new StrategyFineLineSPCluster();

		fineline.setSizeProfileObj(getSizeProfileDTO().toString());
		fineline.setStrategyIFineLineId(getStrategyFinelineId(1));
		finelineSPClusterList.add(fineline);
		finelineSPClusterList.add(getClusterZeroFineline());

		styleSPClusterList = new HashSet<>();
		strategyStyleSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		strategyStyleSPCluster.setStrategyStyleSPClusId(StrategyStyleSPClusId.builder().strategyFinelineSPClusId(fineline.getStrategyIFineLineId()).build());
		styleSPClusterList.add(strategyStyleSPCluster);
		fineline.setStrategyStylesSPClusters(styleSPClusterList);

		ccSPClusterList = new HashSet<>();
		ccSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		ccSPCluster.setStrategyCcSPClusId(StrategyCcSPClusId.builder().strategyStyleSPClusId(strategyStyleSPCluster.getStrategyStyleSPClusId()).build());
		ccSPClusterList.add(ccSPCluster);
		strategyStyleSPCluster.setStrategyCcSPClusters(ccSPClusterList);

		List<SizeProfileDTO> updated = getSizeProfileDTO();
		updated.get(0).setAdjustedSizeProfile(adjSizePct);

		when(sizeAndPackService.getEligibleSizeIds(anyString())).thenReturn(Set.of(ahsSizeId));
		when(sizeAndPackService.safeReadSizeObject(anyString())).thenReturn(getSizeProfileDTO());
		when(sizeAndPackService.getTotalSizeProfilePct(objectMapper.writeValueAsString(updated), Set.of(ahsSizeId))).thenReturn(adjSizePct);

		adjustedSizeProfileUpdateMapper.updateFinelineAdjSizeProfile(finelineSPClusterList, ahsSizeId, 1, adjSizePct);

		List<SizeProfileDTO> finelineSizeProfiles = Arrays.asList(
				objectMapper.readValue(finelineSPClusterList.get(0).getSizeProfileObj(), SizeProfileDTO[].class));
		List<SizeProfileDTO> styleSizeProfiles = Arrays.asList(
				objectMapper.readValue(finelineSPClusterList.get(0)
						.getStrategyStylesSPClusters().iterator().next().getSizeProfileObj(), SizeProfileDTO[].class));
		StrategyCcSPCluster ccSPCluster1 = finelineSPClusterList.stream()
				.filter(fl -> fl.getStrategyIFineLineId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
				.findFirst()
				.map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
				.stream()
				.flatMap(Collection::stream)
				.map(StrategyStyleSPCluster::getStrategyCcSPClusters)
				.flatMap(Collection::stream)
				.findFirst().orElse(new StrategyCcSPCluster());
		StrategyCcSPCluster ccSPCluster2 = finelineSPClusterList.stream()
				.filter(fl -> fl.getStrategyIFineLineId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
				.findFirst()
				.map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
				.stream()
				.flatMap(Collection::stream)
				.map(StrategyStyleSPCluster::getStrategyCcSPClusters)
				.flatMap(Collection::stream)
				.findFirst().orElse(new StrategyCcSPCluster());

		List<SizeProfileDTO> ccSizeProfiles1 = Arrays.asList(
				objectMapper.readValue(ccSPCluster1.getSizeProfileObj(), SizeProfileDTO[].class));
		List<SizeProfileDTO> ccSizeProfiles2 = Arrays.asList(
				objectMapper.readValue(ccSPCluster2.getSizeProfileObj(), SizeProfileDTO[].class));

		assertEquals(adjSizePct, finelineSizeProfiles.get(0).getAdjustedSizeProfile());
		assertEquals(adjSizePct, styleSizeProfiles.get(0).getAdjustedSizeProfile());
		assertEquals(50.0, ccSizeProfiles1.get(0).getAdjustedSizeProfile());
		assertEquals(adjSizePct, ccSizeProfiles2.get(0).getAdjustedSizeProfile());

		assertEquals(0.0, ccSPCluster1.getTotalSizeProfilePct());
		assertEquals(adjSizePct, ccSPCluster2.getTotalSizeProfilePct());
	}

	private StrategyFineLineSPCluster getClusterZeroFineline() {
		StrategyStyleSPCluster strategyStyleSPCluster = new StrategyStyleSPCluster();
		StrategyCcSPCluster ccSPCluster = new StrategyCcSPCluster();
		StrategyFineLineSPCluster fineLineSPCluster = new StrategyFineLineSPCluster();

		fineLineSPCluster.setStrategyIFineLineId(getStrategyFinelineId(0));
		fineLineSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		strategyStyleSPCluster.setStrategyStyleSPClusId(StrategyStyleSPClusId.builder().strategyFinelineSPClusId(fineLineSPCluster.getStrategyIFineLineId()).build());
		strategyStyleSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		ccSPCluster.setStrategyCcSPClusId(StrategyCcSPClusId.builder().strategyStyleSPClusId(strategyStyleSPCluster.getStrategyStyleSPClusId()).build());
		ccSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
		ccSPCluster.setTotalSizeProfilePct(0.0);

		strategyStyleSPCluster.setStrategyCcSPClusters(Set.of(ccSPCluster));
		fineLineSPCluster.setStrategyStylesSPClusters(Set.of(strategyStyleSPCluster));
		return fineLineSPCluster;
	}

	public static List<SizeProfileDTO> getSizeProfileDTO() {
		List<SizeProfileDTO> spoDto = new ArrayList<>();
		SizeProfileDTO sizeProfile = new SizeProfileDTO();
		sizeProfile.setAdjustedSizeProfile(50.0);
		sizeProfile.setSizeProfilePrcnt(17.0);
		sizeProfile.setIsEligible(0);
		sizeProfile.setAhsSizeId(246);
		sizeProfile.setSizeDesc("S/M");
		spoDto.add(sizeProfile);
		return spoDto;
	}

	private StrategyFineLineSPClusId getStrategyFinelineId(Integer clusterId) {
		return StrategyFineLineSPClusId.builder()
				.strategySubCatgSPClusId(StrategySubCatgSPClusId.builder()
						.strategyMerchCatgSPClusId(StrategyMerchCatgSPClusId.builder()
								.planClusterStrategyId(PlanClusterStrategyId.builder()
										.analyticsClusterId(clusterId)
										.build())
								.build())
						.build())
				.build();
	}

}
