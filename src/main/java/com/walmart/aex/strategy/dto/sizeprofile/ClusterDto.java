package com.walmart.aex.strategy.dto.sizeprofile;

import java.util.List;

import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import lombok.Data;

@Data
public class ClusterDto {

	private Integer clusterID;
	private List<SizeProfileDTO> sizes;
	private Double totalSizeProfilePct;

}

