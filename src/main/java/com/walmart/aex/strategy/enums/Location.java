package com.walmart.aex.strategy.enums;

public enum Location {
    ALL_STORES("All Stores"),
    LOCALIZED("Localized");

    private final String value;

    Location(String value) { this.value = value; }
    public String getValue() {
        return this.value;
    }
}
