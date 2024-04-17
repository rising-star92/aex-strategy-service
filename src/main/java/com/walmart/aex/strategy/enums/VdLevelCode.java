package com.walmart.aex.strategy.enums;

import lombok.Getter;

@Getter
public enum VdLevelCode {
    Fineline,
    Sub_Category,
    Category;

    public static VdLevelCode fromString(String value) {

        return value!= null ? VdLevelCode.valueOf(value) : null;
    }



}
