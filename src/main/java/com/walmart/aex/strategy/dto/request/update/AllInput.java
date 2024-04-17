package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class AllInput {
    private List<Field> updatedFields;
}
