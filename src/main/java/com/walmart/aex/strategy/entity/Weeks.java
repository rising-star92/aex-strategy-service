package com.walmart.aex.strategy.entity;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "weeks", schema = "dbo")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Weeks {
    @Id
    @Column(name = "wm_yr_wk")
    private Integer wmYearWeek;

    @Column(name = "fiscal_year_id")
    private Integer fiscalYearId;

    @Column(name = "fiscal_week_desc")
    private String fiscalWeekDesc;

    @Column(name = "wm_week_code", nullable = false)
    private String wmWeekDesc;

    @Column(name = "season_code", nullable = false)
    private String seasonCode;

    @Column(name = "wm_week", nullable = false)
    private Integer wmWeek;

    @MapsId(value = "fiscalYearId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_year_id", nullable = false)
    private FiscalYear fiscalYear;
}