package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.Set;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "strat_fl", schema = "dbo")
public class StrategyFineline {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyFinelineId strategyFinelineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StrategySubCatg strategySubCatg;

    @Column(name = "fineline_desc")
    private String finelineDesc;

    @Column(name = "alt_fineline_desc")
    private String altFinelineName;

    @Column(name = "trait_choice_code")
    private Integer traitChoiceCode;

    @Column(name = "channel_id")
    private Integer channelId;

    @Column(name = "outfitting_fl_list")
    private String outFitting;

    @Column(name = "rpt_lvl_0_gen_desc1")
    private String lvl0GenDesc1;

    @Column(name = "rpt_lvl_1_gen_desc1")
    private String lvl1GenDesc1;

    @Column(name = "rpt_lvl_2_gen_desc1")
    private String lvl2GenDesc1;

    @Column(name = "rpt_lvl_3_gen_desc1")
    private String lvl3GenDesc1;

    @Column(name = "rpt_lvl_4_gen_desc1")
    private String lvl4GenDesc1;

    @Column(name = "run_status_code")
    private Integer runStatusCode;

    @Column(name = "alloc_run_type_code")
    private Integer allocTypeCode;

    @Column(name = "rfa_status_code")
    private Integer rfaStatusCode;

    @Column(name = "brand_obj")
    private String brands;

    @Column(name = "ahs_v_id")
    private Integer productDimId;

    @OneToMany(mappedBy = "strategyFineline", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyStyle> strategyStyles;

    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
    private ChannelText channelText;

    @JoinColumn(name = "alloc_run_type_code", insertable = false, updatable = false)
    @OneToOne(targetEntity = AllocationRunTypeText.class, fetch = FetchType.LAZY)
    private AllocationRunTypeText allocationRunTypeText;

    @JoinColumn(name = "run_status_code", insertable = false, updatable = false)
    @OneToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runStatusText;

    @JoinColumn(name = "rfa_status_code", insertable = false, updatable = false)
    @OneToOne(targetEntity = RfaStatusText.class, fetch = FetchType.LAZY)
    private RfaStatusText rfaStatusText;

}
