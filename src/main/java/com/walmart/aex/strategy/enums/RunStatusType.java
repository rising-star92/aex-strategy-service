package com.walmart.aex.strategy.enums;

public enum RunStatusType {
    UNSELECTED(0, "Unselected"),
    SELECTED(1, "Selected"),
    HIDE(2, "Hide");

    private final Integer id;
    private final String description;

     RunStatusType(final Integer id, final String description) {
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
     * @return the description
     */
    public final String getDescription() {
        return description;
    }


}
