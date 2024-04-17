package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.MetadataSPResponse;
import lombok.Data;

@Data
public class CustomerChoiceSP {


    private String ccId;
    private String colorName;
    private String colorFamily;
    private String channel;
    private String altCcDesc;
    private UpdatedSizesSP updatedSizes;
    private StrategySP strategy;
    private MetadataSPResponse metadata;
}
