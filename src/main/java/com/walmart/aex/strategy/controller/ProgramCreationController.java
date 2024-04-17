package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.Application;
import com.walmart.aex.strategy.dto.GenerateProgramsDTO;
import com.walmart.aex.strategy.service.ProgramCreationService;
import com.walmart.aex.strategy.service.ProgramCreationServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@Slf4j
public class ProgramCreationController {

    private ProgramCreationService programCreationService;


    private ProgramCreationServiceV2 programCreationServiceV2;

    ProgramCreationController(ProgramCreationService programCreationService, ProgramCreationServiceV2 programCreationServiceV2){
        this.programCreationServiceV2 = programCreationServiceV2;
        this.programCreationService = programCreationService;
    }

    @PostMapping(path = "/generatePrograms",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public @ResponseBody
    ResponseEntity excelTemplate(@RequestParam(name = "file") MultipartFile file)  {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=programs.xlsx");
            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(programCreationService.generatePrograms(file.getInputStream())));
        } catch (Exception e){
            log.error("Error occurred while loading the file",e);
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @PostMapping(path = "/v2/generatePrograms",produces = "application/json")
    public @ResponseBody
    ResponseEntity downloadExcel(@RequestBody GenerateProgramsDTO generateProgramsDTO)  {
        try{
            return ResponseEntity.ok(programCreationServiceV2.generateProgramsV2(generateProgramsDTO));
        } catch (Exception e){
            log.error("Error occurred while fetching the file",e);
            return ResponseEntity.internalServerError().body(e);
        }
    }



}
