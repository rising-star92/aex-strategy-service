package com.walmart.aex.strategy.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fiscal_years", schema = "dbo")
public class FiscalYear {
    @Id
    @Column(name = "fiscal_year_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fiscalYearId;

    @Column(name = "fiscal_year_desc", nullable = false)
    private String fiscalYearDesc;

    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYearValue;

    @Column(name = "wm_year", nullable = false)
    private Integer wmtYear;

    @OneToMany(mappedBy = "fiscalYear", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Weeks> weeks;
}
