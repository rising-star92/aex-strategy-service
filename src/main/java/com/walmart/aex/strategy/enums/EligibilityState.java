package com.walmart.aex.strategy.enums;

import java.util.stream.Stream;

public enum EligibilityState {
    ELIGIBLE(1, "Eligible"),
    NOT_ELIGIBLE(0, "Not_Eligible"),
    UNDEFINED(2, "Undefined");

    private Integer id;
    private String description;

    private EligibilityState(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public final void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }


    public static String getEligibilityNameFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().map(EligibilityState::getDescription).orElse(null);
    }

    public static Integer getEligibilityIdFromName(String channelDesc) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(channelDesc))
                .findFirst().map(EligibilityState::getId).orElse(null);
    }
}
