package com.walmart.aex.strategy.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walmart.aex.strategy.dto.sizeprofile.Metrics;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SizeProfileDTO {
    @JsonIgnore
    private ClusterType type;
    private Integer ahsSizeId;
    private String sizeDesc;
    private Double sizeProfilePrcnt;
    private Double adjustedSizeProfile;
    private Integer isEligible;
    private Metrics metrics;

    @Override
    public String toString() {
        return "{" +
                "\"ahsSizeId\":" + ahsSizeId +
                ", \"sizeDesc\":\"" + sizeDesc + '\"' +
                ", \"sizeProfilePrcnt\":" + sizeProfilePrcnt +
                ", \"adjustedSizeProfile\":" + adjustedSizeProfile +
                ", \"isEligible\":" + isEligible +
                ", \"metrics\":" + metrics +
                '}';
    }
}

