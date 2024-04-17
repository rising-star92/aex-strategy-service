package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.request.FiscalWeek;
import com.walmart.aex.strategy.dto.request.WeatherCluster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommonUtilTest {

    @InjectMocks
    private CommonUtil commonUtil;

    @Test
    void testGetInStoreYrWkDesc(){
        WeatherCluster weatherCluster = new WeatherCluster();
        FiscalWeek inStoreFiscalWeek = new FiscalWeek();
        inStoreFiscalWeek.setFiscalWeekDesc("FYE2023WK01");
        inStoreFiscalWeek.setWmYearWeek(12301);
        inStoreFiscalWeek.setDwWeekId(202301);

        weatherCluster.setInStoreDate(inStoreFiscalWeek);

        //Act
        String response = CommonUtil.getInStoreYrWkDesc(weatherCluster);

        //Assert
        assertEquals("FYE2023WK01",response);

    }

    @Test
    void testGetInStoreYrWk(){
        WeatherCluster weatherCluster = new WeatherCluster();
        FiscalWeek inStoreFiscalWeek = new FiscalWeek();
        inStoreFiscalWeek.setFiscalWeekDesc("FYE2023WK01");
        inStoreFiscalWeek.setWmYearWeek(12301);
        inStoreFiscalWeek.setDwWeekId(202301);
        weatherCluster.setInStoreDate(inStoreFiscalWeek);
        //Act
        Integer response = CommonUtil.getInStoreYrWk(weatherCluster);

        //Assert
        assertEquals(12301,response);

    }

    @Test
    void testGetMarkDownYrWk(){
        WeatherCluster weatherCluster = new WeatherCluster();
        FiscalWeek markDownFiscalWeek = new FiscalWeek();
        markDownFiscalWeek.setFiscalWeekDesc("FYE2023WK12");
        markDownFiscalWeek.setWmYearWeek(12312);
        markDownFiscalWeek.setDwWeekId(202312);
        weatherCluster.setMarkDownDate(markDownFiscalWeek);
        //Act
        Integer response = CommonUtil.getMarkdownYrWk(weatherCluster);

        //Assert
        assertEquals(12312,response);

    }

    @Test
    void testGetMarkDownYrWkDesc(){
        WeatherCluster weatherCluster = new WeatherCluster();
        FiscalWeek markDownFiscalWeek = new FiscalWeek();
        markDownFiscalWeek.setFiscalWeekDesc("FYE2023WK12");
        markDownFiscalWeek.setWmYearWeek(12312);
        markDownFiscalWeek.setDwWeekId(202312);
        weatherCluster.setMarkDownDate(markDownFiscalWeek);
        //Act
        String response = CommonUtil.getMarkdownYrWkDesc(weatherCluster);

        //Assert
        assertEquals("FYE2023WK12",response);

    }

}
