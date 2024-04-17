package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "program", schema = "dbo")
public class TraitProgram {

    @Id
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "aex_program_name")
    private String programName;

    @Column(name = "trait_nbr")
    private Integer traitNbr;

    @OneToMany(mappedBy = "traitProgram", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    Set<TraitProgramStore> traitProgramStoreSet;
}
