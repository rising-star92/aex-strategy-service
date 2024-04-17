package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="program_mod_dept_catg", schema = "dbo")
public class ProgramModCat {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProgramModCatId programModCatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private Program program;


    @Column(name = "modular_catg_desc", nullable = false)
    private String modCatDesc;


    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "create_ts", nullable = false)
    private LocalDate createDate;

    @Column(name = "last_modified_ts", nullable = false)
    private LocalDate updateDate;

    @Column(name = "create_userid", nullable = false)
    private String createUserId;

    @Column(name = "last_modified_userid", nullable = false)
    private String lastModifiedUserId;


}
