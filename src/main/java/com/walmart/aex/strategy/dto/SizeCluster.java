package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.ClusterType;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeCluster {
    private ClusterType type;
    private List<SizeProfileDTO> sizeProfiles;
}
