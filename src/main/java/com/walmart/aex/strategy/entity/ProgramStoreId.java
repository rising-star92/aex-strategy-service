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
public class ProgramStoreId implements Serializable {
    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "store_nbr", nullable = false)
    private Long storeNbr;
}
