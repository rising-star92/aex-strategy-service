package com.walmart.aex.strategy.dto.sizeprofile;

import com.walmart.aex.strategy.dto.MetadataSPResponse;
import lombok.Data;

import java.util.List;
@Data
public class StyleDto {
    private String styleNbr;
    private Metrics metrics;
    private List<ClusterDto> clusters ;
    private List<CustomerChoiceList> customerChoices;
    private MetadataSPResponse metadata;
}
