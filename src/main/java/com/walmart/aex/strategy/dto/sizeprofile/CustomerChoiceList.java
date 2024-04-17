package com.walmart.aex.strategy.dto.sizeprofile;
import com.walmart.aex.strategy.dto.MetadataSPResponse;
import lombok.Data;

import java.util.List;

@Data
public class CustomerChoiceList {
    private String ccId;
    private String colorFamily;
    private Metrics metrics;
    private List<ClusterDto> clusters;
    private MetadataSPResponse metadata;
}
