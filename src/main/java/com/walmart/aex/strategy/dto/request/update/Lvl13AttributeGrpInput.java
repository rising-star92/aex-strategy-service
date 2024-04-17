package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class Lvl13AttributeGrpInput {

    private Integer lvl3Nbr;
    private List<Lvl14AttributeGrpInput> lvl4List;
    private List<AttributeFields> updatedFields;
}
