package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "strat_fl_vg", schema = "dbo")
@Builder
public class FpFinelineVDCategory {
    @EmbeddedId
    private FpFinelineVDCategoryId fpFinelineVDCategoryId;


}
