package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.request.Field;
import lombok.Data;

import java.util.List;

@Data
public class UpdatedSizesSP {
    private List<Field> sizes;
}
