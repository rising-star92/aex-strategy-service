package com.walmart.aex.strategy.enums;

import java.util.stream.Stream;

public enum StratGroupType {
    WEATHER_CLUSTER(1, "Finelines ranked within weather clusters"),
    LINE_PLANNING(9, "Line Planning"),
    FIXTURE(10, "Fixture Merchandise Method"),
    SIZE_PROFILE(3,"Size Only Profiles"),
    PRESENTATION_UNITS(8, "Set Presentation Units for AP"),
    VOLUME_DEVIATION_GROUPING(6,"Store Volume Grouping");
    private Integer strategyGroupTypeCode;
    private String strategyGroupDesc;

    private StratGroupType(Integer strategyGroupTypeCode, String strategyGroupDesc) {
        this.strategyGroupTypeCode = strategyGroupTypeCode;
        this.strategyGroupDesc = strategyGroupDesc;
    }

    /**
     * @return the id
     */
    public final Integer getStrategyGroupTypeCode() {
        return strategyGroupTypeCode;
    }

    /**
     * @param strategyGroupTypeCode the strategyGroupTypeCode to set
     */
    public final void setId(Integer strategyGroupTypeCode) {
        this.strategyGroupTypeCode = strategyGroupTypeCode;
    }

    /**
     * @return the description
     */
    public final String getStrategyGroupDesc() {
        return strategyGroupDesc;
    }

    /**
     * @param strategyGroupDesc the strategyGroupDesc to set
     */
    public final void setStrategyGroupDesc(String strategyGroupDesc) {
        this.strategyGroupDesc = strategyGroupDesc;
    }


    public static String getStrategyGroupDescFromTypeCode(Integer strategyGroupTypeCode) {
        return Stream.of(values())
                .filter(e -> e.strategyGroupTypeCode.equals(strategyGroupTypeCode))
                .findFirst().map(StratGroupType::getStrategyGroupDesc).orElse(null);
    }

    public static Integer getStrategyGroupTypeCodeFromDesc(String strategyGroupDesc) {
        return Stream.of(values())
                .filter(e -> e.strategyGroupDesc.equalsIgnoreCase(strategyGroupDesc))
                .findFirst().map(StratGroupType::getStrategyGroupTypeCode).orElse(null);
    }
}
