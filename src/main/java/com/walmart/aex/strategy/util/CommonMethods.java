package com.walmart.aex.strategy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.request.Brands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CommonMethods {
    @Autowired
    private ObjectMapper objectMapper;

    public String getBrandAttributeString(List<Brands> brands) {
        String brandObj = null;
        try {
            brandObj = objectMapper.writeValueAsString(brands);
        } catch (JsonProcessingException e) {
            log.error("Error while converting Brand Obj to String - exception {}", e);
        }
        return brandObj;
    }

    public List<Brands> getBrandAttributes(String brandObj) {
        List<Brands> brands =new ArrayList<>();
        try {
            brands = objectMapper.readValue(brandObj, new TypeReference<List<Brands>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error while reading Brand Obj to collection - exception {}", e);
        }
        return brands;
    }
}
