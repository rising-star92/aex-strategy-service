package com.walmart.aex.strategy.dto.assortproduct;

import lombok.Data;
import java.util.List;

@Data
public class RFASpaceResponse {
    private List<RFAFinelineData> rfaFinelineData;
    private List<RFAStylesCcData> rfaStylesCcData;
}