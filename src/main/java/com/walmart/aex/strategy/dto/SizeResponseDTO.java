package com.walmart.aex.strategy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SizeResponseDTO {
    private Long planId;
    private Long strategyId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private String catSizeObj;
    private Integer lvl4Nbr;
    private String subCategorySizeObj;
    private Integer finelineNbr;
    private String altFinelineName;
    private Integer fixtureTypeId;
    private Integer merchMethodCode;
    private String fineLineSizeObj;
    private String lvl0GenDesc1;
    private String lvl1GenDesc1;
    private String lvl2GenDesc1;
    private String lvl3GenDesc1;
    private String lvl4GenDesc1;
    private String finelineDesc;
    private String styleNbr;
    private String altStyleDesc;
    private String ccId;
    private String altCcDesc;
    private String ccSizeObj;
    private String styleSizeObj;
    private Integer clusterId;
    private String colorName;
    private String colorFamily;
    private Double totalSizeProfilePct;

    public SizeResponseDTO(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String catSizeObj,
                           Integer lvl4Nbr, String subCategorySizeObj, Integer finelineNbr, String fineLineSizeObj, String lvl0GenDesc1,
                           String lvl1GenDesc1, String lvl2GenDesc1, String lvl3GenDesc1, String lvl4GenDesc1, String finelineDesc, String altFinelineName) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.catSizeObj = catSizeObj;
        this.lvl4Nbr = lvl4Nbr;
        this.subCategorySizeObj = subCategorySizeObj;
        this.finelineNbr = finelineNbr;
        this.fineLineSizeObj = fineLineSizeObj;
        this.lvl0GenDesc1 = lvl0GenDesc1;
        this.lvl1GenDesc1 = lvl1GenDesc1;
        this.lvl2GenDesc1 = lvl2GenDesc1;
        this.lvl3GenDesc1 = lvl3GenDesc1;
        this.lvl4GenDesc1 = lvl4GenDesc1;
        this.finelineDesc = finelineDesc;
        this.altFinelineName = altFinelineName;
    }

    public SizeResponseDTO(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr, String altStyleDesc, String ccId, String altCcDesc, String colorName, String colorFamily, String ccSizeObj) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.altStyleDesc = altStyleDesc;
        this.ccId = ccId;
        this.altCcDesc = altCcDesc;
        this.colorName = colorName;
        this.colorFamily = colorFamily;
        this.ccSizeObj = ccSizeObj;
    }

    public SizeResponseDTO(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String catSizeObj,
                           Integer lvl4Nbr, String subCategorySizeObj, Integer finelineNbr, String fineLineSizeObj, String lvl0GenDesc1,
                           String lvl1GenDesc1, String lvl2GenDesc1, String lvl3GenDesc1, String lvl4GenDesc1, String finelineDesc, String altFinelineName,
                           String styleNbr, String ccId, String ccSizeObj) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.catSizeObj = catSizeObj;
        this.lvl4Nbr = lvl4Nbr;
        this.subCategorySizeObj = subCategorySizeObj;
        this.finelineNbr = finelineNbr;
        this.fineLineSizeObj = fineLineSizeObj;
        this.lvl0GenDesc1 = lvl0GenDesc1;
        this.lvl1GenDesc1 = lvl1GenDesc1;
        this.lvl2GenDesc1 = lvl2GenDesc1;
        this.lvl3GenDesc1 = lvl3GenDesc1;
        this.lvl4GenDesc1 = lvl4GenDesc1;
        this.finelineDesc = finelineDesc;
        this.altFinelineName = altFinelineName;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.ccSizeObj = ccSizeObj;
    }



    public SizeResponseDTO(Long planId, Long strategyId,Integer clusterId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr,
                           Integer lvl4Nbr, Integer finelineNbr, String fineLineSizeObj) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.clusterId = clusterId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.fineLineSizeObj = fineLineSizeObj;
    }

    public SizeResponseDTO(Long planId, Long strategyId, Integer clusterId,Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr,
                           Integer lvl4Nbr, Integer finelineNbr, String styleNbr , String styleSizeObj) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.clusterId = clusterId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.styleSizeObj = styleSizeObj;
    }

    public SizeResponseDTO(Long planId, Long strategyId,Integer clusterId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr,
                           Integer finelineNbr, String styleNbr, String ccId, String ccSizeObj) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.clusterId = clusterId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.ccSizeObj = ccSizeObj;
    }

    public SizeResponseDTO(Long planId, Long strategyId,Integer clusterId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr,
                           Integer finelineNbr, Integer fixtureTypeId, Integer merchMethodCode, String styleNbr, String ccId, String ccSizeObj, String colorFamily,
                           Double totalSizeProfilePct) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.clusterId = clusterId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.fixtureTypeId = fixtureTypeId;
        this.merchMethodCode = merchMethodCode;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.ccSizeObj = ccSizeObj;
        this.colorFamily = colorFamily;
        this.totalSizeProfilePct = totalSizeProfilePct;
    }

    public SizeResponseDTO(Integer finelineNbr, String styleNbr, String ccId, Integer clusterId, Integer fixtureTypeId, Integer merchMethodCode, Double totalSizeProfilePct) {
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.clusterId = clusterId;
        this.fixtureTypeId = fixtureTypeId;
        this.merchMethodCode = merchMethodCode;
        this.totalSizeProfilePct = totalSizeProfilePct;
    }

    public SizeResponseDTO(Integer finelineNbr, String styleNbr, String ccId, Integer clusterId, Double totalSizeProfilePct) {
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.clusterId = clusterId;
        this.totalSizeProfilePct = totalSizeProfilePct;
    }
}