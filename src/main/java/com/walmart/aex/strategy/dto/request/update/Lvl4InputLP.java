package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class Lvl4InputLP {
    private Integer lvl4Nbr;
    private String lvl4Name;
    private List<Field> updatedFields;

}
