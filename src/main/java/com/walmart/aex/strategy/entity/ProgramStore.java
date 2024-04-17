package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "program_store", schema = "dbo")
public class ProgramStore {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProgramStoreId programStoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private Program program;

    @Column(name = "st_effective_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "st_expiration_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "bu_id", nullable = false)
    private Integer buId;


}
