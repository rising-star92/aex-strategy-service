package com.walmart.aex.strategy.dto.sizeprofile;
import com.walmart.aex.strategy.dto.MetadataSPResponse;
import lombok.Data;

import java.util.List;

@Data
public class FineLine {
    private Integer finelineNbr;
    private String finelineDesc;
    private Metrics metrics;
    private List<ClusterDto> clusters ;
    private List<StyleDto> styles;
    private List<MerchMethodsDto> merchMethods;
    private MetadataSPResponse metadata;
}
