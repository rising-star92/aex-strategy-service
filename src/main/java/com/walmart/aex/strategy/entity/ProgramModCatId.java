package com.walmart.aex.strategy.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ProgramModCatId implements Serializable {
    @Column(name = "program_id", nullable = false)
    private Integer programId;

    @Column(name = "modular_dept_nbr", nullable = false)
    private Integer modDeptNbr;

    @Column(name = "modular_catg_nbr", nullable = false)
    private Integer modCatNbr;
}
