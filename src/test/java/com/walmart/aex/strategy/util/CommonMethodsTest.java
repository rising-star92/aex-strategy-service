package com.walmart.aex.strategy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.request.Brands;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CommonMethodsTest {
    @InjectMocks
    private CommonMethods commonMethods;


    @Test
    void testCovertBrandCollectionToString(){
        ReflectionTestUtils.setField(commonMethods, "objectMapper", new ObjectMapper());
        List<Brands> brands = new ArrayList<>();
        Brands brand1 = new Brands();
        brand1.setBrandId(1);
        brand1.setBrandName("Brand1");
        brand1.setBrandLabelCode(null);
        brand1.setBrandType(null);

        Brands brand2 = new Brands();
        brand2.setBrandId(2);
        brand2.setBrandName("Brand2");
        brand2.setBrandLabelCode(null);
        brand2.setBrandType(null);

        brands.add(brand1);
        brands.add(brand2);

        String brandObj = commonMethods.getBrandAttributeString(brands);
        assertTrue(brandObj.contains("Brand2"));
    }

    @Test
    void testCovertBrandObjToCollection(){
        ReflectionTestUtils.setField(commonMethods, "objectMapper", new ObjectMapper());
        String brandObj = "[" +
                "    {" +
                "        \"brandId\": null,\n" +
                "        \"brandLabelCode\": null,\n" +
                "        \"brandName\": \"License\",\n" +
                "        \"brandType\": null\n" +
                "    }\n" +
                "]";

        List<Brands> brands = commonMethods.getBrandAttributes(brandObj);
        assertEquals(1,brands.stream().count());
    }
}
