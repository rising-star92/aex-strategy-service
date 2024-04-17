package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.MetadataSPResponse;
import lombok.Data;

import java.util.List;

@Data
public class StyleSP {

    private String styleNbr;
    private String channel;
    private String altStyleDesc;
    private UpdatedSizesSP updatedSizes;
    private MetadataSPResponse metadata;
    private List<CustomerChoiceSP> customerChoices;
}
