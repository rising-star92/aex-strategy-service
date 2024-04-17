package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class Lvl3InputLP {
    private Integer lvl3Nbr;
    private List<Lvl4InputLP> lvl4List;
    private List<Field> updatedFields;
}
