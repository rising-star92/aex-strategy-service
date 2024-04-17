package com.walmart.aex.strategy.enums;

import java.util.stream.Stream;

public enum TraitChoiceType {

    TRAITED(1, "Traited"),
    NONTRAITED(2, "Non Traited"),
    BOTH(3, "Both");

    private Integer code;
    private String description;

    private TraitChoiceType(Integer code, String description) {
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


    public static String getTraitChoiceFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.code.equals(id))
                .findFirst().map(TraitChoiceType::getDescription).orElse(null);
    }

    public static Integer getTraitChoiceCodeFromName(String traitDesc) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(traitDesc))
                .findFirst().map(TraitChoiceType::getCode).orElse(null);
    }
}
