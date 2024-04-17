package com.walmart.aex.strategy.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.collection.internal.PersistentList;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="program", schema = "dbo")
public class Program {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "program_id", nullable = false)
    private Integer programId;

    @Column(name = "aex_program_name", nullable = false)
    private String programName;

    @Column(name = "trait_nbr", nullable = false)
    private Integer traitNbr;

    @Column(name = "merch_dept_nbr", nullable = false)
    private Integer deptNbr;

    @Column(name = "trait_short_desc", nullable = false)
    private String shortDesc;

    @Column(name = "trait_long_desc", nullable = false)
    private String longDesc;

    @Column(name = "pgm_create_userid", nullable = false)
    private String userId;

    @Column(name = "acct_dept_nbr", nullable = false)
    private Integer acctDeptNbr;

    @Column(name = "pgm_create_ts", nullable = false)
    private LocalDate createDate;

    @Column(name = "pgm_last_change_ts", nullable = false)
    private LocalDate updateDate;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProgramStore> programStores;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProgramModCat> programModCats;



    public void setProgramStores(Set<ProgramStore> programStores) {
       if(this.programStores != null){
            this.programStores.clear();
            this.programStores.addAll(programStores);
        }  else {
            this.programStores = programStores;
        }
    }

    public void setProgramModCats(Set<ProgramModCat> programModCats) {
        if(this.programModCats != null){
            this.programModCats.clear();
            this.programModCats.addAll(programModCats);
        }  else {
            this.programModCats = programModCats;
        }
    }


}
