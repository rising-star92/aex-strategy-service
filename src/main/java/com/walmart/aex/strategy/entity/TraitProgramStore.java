package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "program_store", schema = "dbo")
public class TraitProgramStore {

    @EmbeddedId
    private TraitProgramStoreId traitProgramStoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private TraitProgram traitProgram;

    @Column(name = "country_code")
    private String CountryCode;

    @Column(name = "state_province_code")
    private String stateCode;
}
