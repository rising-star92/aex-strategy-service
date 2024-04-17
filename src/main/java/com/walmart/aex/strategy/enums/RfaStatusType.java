package com.walmart.aex.strategy.enums;

public enum RfaStatusType {
    RFA_IN_PROGRESS(1, "RFA Inprogress"),
    RFA_NOT_RUN(2, "RFA have not Run"),
    RFA_ALLOCATED(3, "RFA Allocated"),
    RFA_FAILED(4, "RFA Failed"),
    RFA_NOT_ALLOCATED(5, "RFA Not Allocated");

    private final Integer id;
    private final String description;

     RfaStatusType(final Integer id,final  String description) {
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
