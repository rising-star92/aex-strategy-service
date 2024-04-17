package com.walmart.aex.strategy.dto.sizeprofile;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SizeProfileMapperDTO {
    private SizeResponseDTO sizeResponseDTO;
    private PlanSizeProfile response;
    private String channel;
    private String styleNbr;
    private String ccId;
    private Set<Integer> eligibleAhsSizeIds;
    private List<SizeResponseDTO> validationResponseList;
}
