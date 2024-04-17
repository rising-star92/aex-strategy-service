package com.walmart.aex.strategy.enums;

import java.util.stream.Stream;

public enum FixtureTypeRollup {

    WALLS(1, "WALL"),
    ENDCAPS(2, "ENDCAP"),
    RACKS(3, "RACK"),
    TABLES(4, "TABLE"),
    H_RACKS(5, "H-RACK");

    private Integer code;
    private String description;

    private FixtureTypeRollup(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return the id
     */
    public final Integer getCode() {
        return code;
    }

    /**
     * @param code the id to set
     */
    public final void setCode(Integer code) {
        this.code = code;
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


    public static String getFixtureTypeFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.code.equals(id))
                .findFirst().map(FixtureTypeRollup::getDescription).orElse(null);
    }

    public static Integer getFixtureIdFromName(String fixtureType) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(fixtureType))
                .findFirst().map(FixtureTypeRollup::getCode).orElse(null);
    }

    public static Integer getFixtureIdFromNamePluralSingular(String fixtureType) {
        final String fixtureTypeIn = fixtureType.toUpperCase().replace("S","");
        return Stream.of(values())
                .filter(e -> e.description.toUpperCase().replace("S","").equalsIgnoreCase(fixtureTypeIn))
                .findFirst().map(FixtureTypeRollup::getCode).orElse(null);
    }
}
