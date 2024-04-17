package com.walmart.aex.strategy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class MerchMethodResponse {
        private Long planId;
        private Long strategyId;
        private Integer lvl0Nbr;
        private Integer lvl1Nbr;
        private Integer lvl2Nbr;
        private Integer lvl3Nbr;
        private Integer lvl4Nbr;
        private Integer finelineNbr;
        private Integer fixtureTypeId;
        private Integer channelId;
        private String lvl0GenDesc1;
        private String lvl1GenDesc1;
        private String lvl2GenDesc1;
        private String lvl3GenDesc1;
        private String lvl4GenDesc1;
        private String finelineDesc;
        private String  altFinelineName;
        private Integer merchMethodCode;

        public MerchMethodResponse(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                                   Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer fixtureTypeId, Integer channelId,
                                   String lvl0GenDesc1, String lvl1GenDesc1, String lvl2GenDesc1, String lvl3GenDesc1,
                                   String lvl4GenDesc1, String finelineDesc) {
                this.planId = planId;
                this.strategyId = strategyId;
                this.lvl0Nbr = lvl0Nbr;
                this.lvl1Nbr = lvl1Nbr;
                this.lvl2Nbr = lvl2Nbr;
                this.lvl3Nbr = lvl3Nbr;
                this.lvl4Nbr = lvl4Nbr;
                this.finelineNbr = finelineNbr;
                this.fixtureTypeId = fixtureTypeId;
                this.channelId = channelId;
                this.lvl0GenDesc1 = lvl0GenDesc1;
                this.lvl1GenDesc1 = lvl1GenDesc1;
                this.lvl2GenDesc1 = lvl2GenDesc1;
                this.lvl3GenDesc1 = lvl3GenDesc1;
                this.lvl4GenDesc1 = lvl4GenDesc1;
                this.finelineDesc = finelineDesc;
        }

        public MerchMethodResponse(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                                   Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer fixtureTypeId, Integer channelId,
                                   String lvl0GenDesc1, String lvl1GenDesc1, String lvl2GenDesc1, String lvl3GenDesc1,
                                   String lvl4GenDesc1, String finelineDesc,String altFinelineName, Integer merchMethodCode) {
                this.planId = planId;
                this.strategyId = strategyId;
                this.lvl0Nbr = lvl0Nbr;
                this.lvl1Nbr = lvl1Nbr;
                this.lvl2Nbr = lvl2Nbr;
                this.lvl3Nbr = lvl3Nbr;
                this.lvl4Nbr = lvl4Nbr;
                this.finelineNbr = finelineNbr;
                this.fixtureTypeId = fixtureTypeId;
                this.channelId = channelId;
                this.lvl0GenDesc1 = lvl0GenDesc1;
                this.lvl1GenDesc1 = lvl1GenDesc1;
                this.lvl2GenDesc1 = lvl2GenDesc1;
                this.lvl3GenDesc1 = lvl3GenDesc1;
                this.lvl4GenDesc1 = lvl4GenDesc1;
                this.finelineDesc = finelineDesc;
                this.altFinelineName = altFinelineName;
                this.merchMethodCode = merchMethodCode;
        }
}
