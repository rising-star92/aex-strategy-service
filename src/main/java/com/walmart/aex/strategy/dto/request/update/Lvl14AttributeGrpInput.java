package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class Lvl14AttributeGrpInput {

    private Integer lvl4Nbr;
    private String lvl4Name;
    private List<AttributeFields> updatedFields;
}
