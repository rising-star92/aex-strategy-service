package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.RFAStatusDataDTO;
import com.walmart.aex.strategy.entity.ProductDimensions;
import lombok.Data;

import java.util.List;

@Data
public class Fineline {
    private Integer finelineNbr;
    private String finelineName;
    private String altFinelineName;
    private String channel;
    private String traitChoice;
    private String outFitting;
    private UpdatedFields updatedFields;
    private Strategy strategy;
    private List<Style> styles;
    private Integer finelineRank;
    private RFAStatusDataDTO allocRunStatus;
    private RFAStatusDataDTO runStatus;
    private RFAStatusDataDTO rfaStatus;
    private List<Brands> brands;
    private ProductDimensions productDimensions;
}
