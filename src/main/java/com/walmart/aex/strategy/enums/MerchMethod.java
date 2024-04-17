package com.walmart.aex.strategy.enums;

import java.util.stream.Stream;

public enum MerchMethod {
    HANGING(1, "HANGING"),
    FOLDED(2, "FOLDED");

    private Integer code;
    private String description;

    private MerchMethod(Integer code, String description) {
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


    public static String getMerchMethodFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.code.equals(id))
                .findFirst().map(MerchMethod::getDescription).orElse(null);
    }

    public static Integer getMerchMethodIdFromName(String merchMethod) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(merchMethod))
                .findFirst().map(MerchMethod::getCode).orElse(null);
    }
}
