package com.walmart.aex.strategy.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;

public class ExcelUtils {
    public static int fetchNumericValueFromCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if(cell != null) {
            try {
                return (int) Math.ceil(cell.getNumericCellValue());
            } catch (Exception e){
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static String fetchStringValueFromCell(Row row, int index) {
        if(row.getCell(index) != null) {
            return row.getCell(index).getStringCellValue().trim();
        } else {
            return "";
        }
    }

    private static Date fetchDateValueFromCell(Row row, int index) {
        return row.getCell(index).getDateCellValue();
    }
}
