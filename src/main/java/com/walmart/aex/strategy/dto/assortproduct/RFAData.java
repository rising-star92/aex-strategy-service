package com.walmart.aex.strategy.dto.assortproduct;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RFAData {
   private Long plan_id_partition;
   private Integer rpt_lvl_0_nbr;
   private Integer rpt_lvl_1_nbr;
   private Integer rpt_lvl_2_nbr;
   private Integer rpt_lvl_3_nbr;
   private Integer rpt_lvl_4_nbr;
}
