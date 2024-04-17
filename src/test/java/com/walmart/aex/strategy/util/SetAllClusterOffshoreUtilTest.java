package com.walmart.aex.strategy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SetAllClusterOffshoreUtilTest {
    @Mock
    private SetAllClusterOffshoreUtil setAllClusterOffshoreUtil;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSetAllClusterOffshore() throws IOException {
        // Arrange
        File responseObject =
                new File(this.getClass().getResource("/PlanStrategyResponse.json").getFile());
        PlanStrategyResponse request = objectMapper.readValue(responseObject, PlanStrategyResponse.class);
        //Act
        setAllClusterOffshoreUtil.setAllClusterOffshoreList(request);
        //Assert
        verify(setAllClusterOffshoreUtil, times(1)).setAllClusterOffshoreList(any());
    }
}
