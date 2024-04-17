package com.walmart.aex.strategy.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;

@ExtendWith(MockitoExtension.class)
class UpdateCalculateSpreadStatusTest {
	 @InjectMocks
	 @Spy
	    SizeEligibilityMapper sizeEligibilityMapper;
	 
	 @Test
	    void UpdateCalculatedSpreadStatus() throws JsonProcessingException {
		
		StrategyCcSPCluster strategyCcSPCluster = new StrategyCcSPCluster();
		SizeProfileDTO sizeDto= new SizeProfileDTO();
		sizeDto.setAhsSizeId(346);
		sizeDto.setSizeDesc("XXL");
		sizeDto.setSizeProfilePrcnt(null);
		sizeDto.setAdjustedSizeProfile(null);
		sizeDto.setIsEligible(0);
		
		List<SizeProfileDTO> filteredSizeProfile=new ArrayList<>();
		filteredSizeProfile.add(sizeDto);
		SizeProfileDTO[] sizeProfiledtos= {sizeDto};
		Map<String,String> updatedSizesIds=new HashMap<String,String>();
		updatedSizesIds.put("346", "1");
		
		sizeEligibilityMapper.setCalcSpreadStatusCc(strategyCcSPCluster,updatedSizesIds,sizeProfiledtos);
		int calculcateSpreadStatusCc= strategyCcSPCluster.getCalcSpSpreadInd();
		assertEquals(1, calculcateSpreadStatusCc);
		
	    }

}
