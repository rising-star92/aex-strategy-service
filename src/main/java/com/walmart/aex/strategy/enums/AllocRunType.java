package com.walmart.aex.strategy.enums;

public enum AllocRunType {
    LOCKED(1,"Locked"),
    UNLOCKED(2, "Unlocked");

    private final Integer id;
    private final String description;

       AllocRunType(final Integer id, final String description) {
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
