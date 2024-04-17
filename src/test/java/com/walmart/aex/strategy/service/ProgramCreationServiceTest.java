package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.ProgramDTO;
import com.walmart.aex.strategy.entity.Program;
import graphql.Assert;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class ProgramCreationServiceTest {

    private CompletableFuture<Integer>  mockedFunction(){
        return CompletableFuture.completedFuture(1);
    }

    @Test
    void testPlanCreation() throws IOException, InvalidFormatException {
        File excelTemplate = new File(this.getClass().getResource("/newFromLatest.xlsx").getFile());
        Workbook workbook = new XSSFWorkbook(excelTemplate);
        Sheet sheet = workbook.getSheetAt(0);
        Map<String, ProgramDTO> result = ProgramCreationService.getGenerateModsFromExcel(sheet);
        Assertions.assertEquals(178, result.size());
        ProgramDTO programDTO = result.get("sofia");
        Assertions.assertNull(programDTO.getProgramId());
        Assertions.assertEquals(2481,programDTO.getStoreList().size());
        Assertions.assertEquals(2,programDTO.getMods().size());
        Assertions.assertEquals("SOFIA",programDTO.getProgramName());
    }

    @Test
    void createExcel() throws IOException {
        List<Program> programList = new ArrayList<>();
        Program program = new Program();
        program.setProgramId(3001);
        program.setProgramName("SOFIA");
        programList.add(program);

        InputStream in =  ProgramCreationService.generateResponseExcel(programList);
        Assert.assertNotNull(in);
    }




}
