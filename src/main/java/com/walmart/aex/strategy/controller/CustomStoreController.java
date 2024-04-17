package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.customstore.CustomStoreRequest;
import com.walmart.aex.strategy.service.CustomStoreService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomStoreController {

    private final CustomStoreService customStoreService;

    public CustomStoreController(CustomStoreService customStoreService) {
        this.customStoreService = customStoreService;
    }

    @GetMapping(path = "/custom-store-excel")
    public ResponseEntity<Resource> fetchCustomStoreListExcel(@RequestBody CustomStoreRequest request) {
        InputStreamResource resource = new InputStreamResource(customStoreService.fetchCustomStoreExcel(request));
        String fileName = request.getProgramName() + " - " + request.getPlanId() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
    }

}
