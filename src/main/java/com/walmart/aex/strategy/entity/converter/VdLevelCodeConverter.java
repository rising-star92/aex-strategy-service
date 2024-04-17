package com.walmart.aex.strategy.entity.converter;

import com.walmart.aex.strategy.enums.VdLevelCode;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class VdLevelCodeConverter implements AttributeConverter<VdLevelCode, Integer> {
    private static final Map<Integer, VdLevelCode> integerToType = new HashMap<>();
    private static final Map<VdLevelCode,Integer> typeToInteger = new HashMap<>();
    static {
        integerToType.put(1,VdLevelCode.Fineline);
        integerToType.put(2,VdLevelCode.Sub_Category);
        integerToType.put(3,VdLevelCode.Category);

        typeToInteger.put(VdLevelCode.Fineline,1);
        typeToInteger.put(VdLevelCode.Sub_Category,2);
        typeToInteger.put(VdLevelCode.Category,3);
    }

    @Override
    public Integer convertToDatabaseColumn(VdLevelCode vdLevelCode) {
        return typeToInteger.get(vdLevelCode);
    }

    @Override
    public  VdLevelCode convertToEntityAttribute (Integer code) {

        return integerToType.get(code);
    }


}
