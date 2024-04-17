package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.Brands;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.entity.StrategyFineline;
import com.walmart.aex.strategy.repository.AllocRunTypeRepository;
import com.walmart.aex.strategy.repository.RunRFARepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.CommonMethods;
import com.walmart.aex.strategy.util.FinelineRFALockingTestUtil;
import com.walmart.aex.strategy.util.RunRFAFetchDTOToFixtureAllocationStrategyMapperUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunRFAServiceTest {

    @InjectMocks
    RunRFAService runRFAService;

    @Mock
    AllocRunTypeRepository allocRunTypeRepository;

    @Mock
    RunRFARepository runRFARepository;

    @Mock
    StrategyGroupRepository strategyGroupRepository;

    @Mock
    StrategyFinelineRepository stratFlRepository;

    PlanStrategyRequest request;
    List<String> keys;
    HashMap<String, Integer> updatingFields;
    List<StrategyFineline> finelines;

    String runStatusCode = "runStatusCode";
    String rfaStatusCode = "rfaStatusCode";
    String allocTypeCode = "allocTypeCode";


    @Test
    void testFetchAllocRunTypesTest(){
        Mockito.when(allocRunTypeRepository.fetAllAllocationRunType()).thenReturn(FinelineRFALockingTestUtil.getAllocationRunTypeTextEntity());
        ReflectionTestUtils.setField(runRFAService,"runRFAStatusFinelineMapper", new RunRFAStatusFinelineMapper());
        List<RFAStatusDataDTO> runTypesList = runRFAService.fetchAllocRunTypes();
        Assertions.assertEquals(2, runTypesList.size());
    }


    @Nested
    @DisplayName("Testing Fetch RFA records")
    class FetchRFAData{
        @BeforeEach
        void SetDataForRun(){
            CommonMethods commonMethods = new CommonMethods();
            ReflectionTestUtils.setField(commonMethods, "objectMapper", new ObjectMapper());

            RunRFAStatusFinelineMapper rfaMapper = new RunRFAStatusFinelineMapper();
            ReflectionTestUtils.setField(rfaMapper, "commonMethods", commonMethods);

            ReflectionTestUtils.setField(runRFAService,"runRFAStatusFinelineMapper", rfaMapper);
        }

        @Test
        void testFetchRunRFAStatusByPlan() {
            String brandObj = "[{\"brandId\":432450,\"brandLabelCode\":null,\"brandName\":\"Adtech\",\"brandType\":null}]";
            FixtureAllocationStrategy mockFixtureAllocationStrategy = new FixtureAllocationStrategy(12L, 3L, 14L, "S3", 2024,  50000, 34, 1488, 12965, "Reebok Activewear Womens",
                    34974, "Fleece Tops Reebok Active Womens",405, "405 - REEBOK CREWNECK SWEATSHIRT",null, null,null ,"Both", 1 , "Locked",
                    0, "Unselected", 3, "RFA Completed", 1);

            RunRFAFetchDTO runRFAFetchDTOMock = new RunRFAFetchDTO() {
                @Override
                public FixtureAllocationStrategy getRunRFAFetchData() {
                    return mockFixtureAllocationStrategy;
                }
            };

            List<RunRFAFetchDTO> runRFAStatusDTOList = new ArrayList<>();
            runRFAStatusDTOList.add(runRFAFetchDTOMock);
            Mockito.doReturn(runRFAStatusDTOList)
                    .when(runRFARepository)
                    .getRunRFAStatusForFinelines(12L, 3L);
            when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(anyInt(), anyLong())).thenReturn(3L);

            PlanStrategyResponse resp = runRFAService.fetchRunRFAStatusByPlan(12L, new PlanStrategyResponse());
            Mockito.verify(runRFARepository, times(1)).getRunRFAStatusForFinelines(12L, 3L);
            assertNotNull(resp);
            assertEquals(12, resp.getPlanId());
        }

        @Test
        void testFetchBrandsForRunRFAByPlan() {
            String brandObj = "[{\"brandId\":432450,\"brandLabelCode\":null,\"brandName\":\"Adtech\",\"brandType\":null}]";
            FixtureAllocationStrategy mockFixtureAllocationStrategy = new FixtureAllocationStrategy(12L, 3L, 14L, "S3", 2024,  50000, 34, 1488, 12965, "Reebok Activewear Womens",
                    34974, "Fleece Tops Reebok Active Womens",405, "405 - REEBOK CREWNECK SWEATSHIRT",null, null,brandObj ,"Both", 1 , "Locked",
                    0, "Unselected", 3, "RFA Completed", 1);

            RunRFAFetchDTO runRFAFetchDTOMock = new RunRFAFetchDTO() {
                @Override
                public FixtureAllocationStrategy getRunRFAFetchData() {
                    return mockFixtureAllocationStrategy;
                }
            };

            List<RunRFAFetchDTO> runRFAStatusDTOList = new ArrayList<>();
            runRFAStatusDTOList.add(runRFAFetchDTOMock);
            Mockito.doReturn(runRFAStatusDTOList)
                    .when(runRFARepository)
                    .getRunRFAStatusForFinelines(12L, 3L);
            when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(anyInt(), anyLong())).thenReturn(3L);

            PlanStrategyResponse resp = runRFAService.fetchRunRFAStatusByPlan(12L, new PlanStrategyResponse());
            Mockito.verify(runRFARepository, times(1)).getRunRFAStatusForFinelines(12L, 3L);
            assertNotNull(resp);

            Fineline fineLine = resp.getLvl3List().stream().findFirst().get().getLvl4List().stream().findFirst().get().getFinelines().stream().findFirst().get();
            assertEquals(true, fineLine.getBrands().size()>0);
        }

        @Test
        void testFetchRunRFAStatusForPlanThatDoesnotExists() {
            RunRFAFetchDTO runRFAFetchDTOMock = new RunRFAFetchDTO() {
                @Override
                public FixtureAllocationStrategy getRunRFAFetchData() {
                    return null;
                }
            };

            List<RunRFAFetchDTO> runRFAStatusDTOList = new ArrayList<>();
            runRFAStatusDTOList.add(runRFAFetchDTOMock);
            Mockito.doReturn(runRFAStatusDTOList)
                    .when(runRFARepository)
                    .getRunRFAStatusForFinelines(12L, 3L);
            when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(anyInt(), anyLong())).thenReturn(3L);

            PlanStrategyResponse resp = runRFAService.fetchRunRFAStatusByPlan(12L, new PlanStrategyResponse());
            Mockito.verify(runRFARepository, times(1)).getRunRFAStatusForFinelines(12L, 3L);
            assertNotNull(resp);
            assertNull( resp.getPlanId());
        }
    }

    @Nested
    @DisplayName("Testing Single Fineline Update with statuses")
    class SingleFineLineUpdateRfaStatus{
        @BeforeEach
        void setDataForRun(){
            keys = new ArrayList<>();
            keys.add("10000,1000,100");

            updatingFields= new HashMap<>();
            finelines = FinelineRFALockingTestUtil.getFinelines().stream().filter(strategyFineline -> strategyFineline.getStrategyFinelineId().getFinelineNbr().equals(100)&&
                    strategyFineline.getStrategyFinelineId().getStrategySubCatgId().getLvl4Nbr().equals(1000)&&
                    strategyFineline.getStrategyFinelineId().getStrategySubCatgId().getStrategyMerchCatgId().getLvl3Nbr().equals(10000)).collect(Collectors.toList());

            Mockito.when(stratFlRepository.findFineLines_ByPlan_Id_AndCat_IdAndSub_Cat_IdAndFineline_nbr(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList())).thenReturn(finelines);
            Mockito.when(stratFlRepository.saveAll(finelines)).thenReturn(finelines);
        }

        @Test
        void testUpdateAllocationTypeWithWithUpdateFieldsOnAll(){
            updatingFields.put(allocTypeCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getAllocTypeCode());
        }

        @Test
        void testUpdateAllocationTypeWithUpdateFieldsOnFL(){
            updatingFields.put(allocTypeCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, false);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getAllocTypeCode());
        }

        @Test
        void testUpdateRFAStatusCodeWithAll(){
            updatingFields.put(rfaStatusCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getRfaStatusCode());
        }

        @Test
        void testUpdateRFAStatusCodeWithUpdateFieldsOnFL(){
            updatingFields.put(rfaStatusCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getRfaStatusCode());
        }

        @Test
        void testUpdateRunStatusCodeWithAll(){
            updatingFields.put(runStatusCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getRunStatusCode());
        }

        @Test
        void testUpdateRunStatusCodeWithUpdateFieldsOnFL(){
            updatingFields.put(runStatusCode, 1);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, false);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(1, fineline.getRunStatusCode());
        }

        @Test
        void testNewFineLineRunStatusCodeShoudlNotChangeForRFAFailure(){
            updatingFields.put(rfaStatusCode, 4);
            finelines.get(0).setRunStatusCode(2);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, false);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(2, fineline.getRunStatusCode());
            Assertions.assertEquals(4, fineline.getRfaStatusCode());
        }

        @Test
        void testUpdateStatusAfterRFACompletion(){
            updatingFields.put(rfaStatusCode, 3);
            updatingFields.put(allocTypeCode, 1);
            updatingFields.put(runStatusCode, 0);
            request = FinelineRFALockingTestUtil.createRequest((long)1,keys, updatingFields, false);
            runRFAService.updateFinelineRFAStatus(request);

            StrategyFineline fineline = finelines.get(0);
            Assertions.assertEquals(3, fineline.getRfaStatusCode());
            Assertions.assertEquals(1, fineline.getAllocTypeCode());
            Assertions.assertEquals(0, fineline.getRunStatusCode());
        }
    }

    @Nested
    @DisplayName("Testing Multiple Fineline Update with statuses")
    class MulipleFineLineUpdateRfaStatus {
        @BeforeEach
        void setDataForRun(){
            keys = new ArrayList<>();
            keys.add("10000,1000,100");
            keys.add("10000,1000,101");
            keys.add("10000,1001,200");
            keys.add("10001,2000,300");
            keys.add("10001,2001,400");
            keys.add("10001,2001,401");

            updatingFields= new HashMap<>();
            finelines = FinelineRFALockingTestUtil.getFinelines();

            Mockito.when(stratFlRepository.findFineLines_ByPlan_Id_AndCat_IdAndSub_Cat_IdAndFineline_nbr(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList())).thenReturn(finelines);
            Mockito.when(stratFlRepository.saveAll(Mockito.anyIterable())).thenReturn(finelines);
        }

        @Test
        void testUpdateAllocationType(){
            updatingFields.put(allocTypeCode, 1);
            request = FinelineRFALockingTestUtil.createRequest(1L,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            finelines.forEach(strategyFineline -> Assertions.assertEquals(1, strategyFineline.getAllocTypeCode()));
        }

        @Test
        void testUpdateRfaStatusCode(){
            updatingFields.put(rfaStatusCode, 2);
            request = FinelineRFALockingTestUtil.createRequest(1L,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            finelines.forEach(strategyFineline -> Assertions.assertEquals(2, strategyFineline.getRfaStatusCode()));
        }

        @Test
        void testUpdateRunStatusCode(){
            updatingFields.put(runStatusCode, 1);
            request = FinelineRFALockingTestUtil.createRequest(1L,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            finelines.forEach(strategyFineline -> Assertions.assertEquals(1, strategyFineline.getRunStatusCode()));
        }

        @Test
        void testUpdateStatusAfterRFACompletion(){
            updatingFields.put(rfaStatusCode, 3);
            updatingFields.put(allocTypeCode, 1);
            updatingFields.put(runStatusCode, 0);
            request = FinelineRFALockingTestUtil.createRequest(1L,keys, updatingFields, true);
            runRFAService.updateFinelineRFAStatus(request);

            finelines.forEach(strategyFineline -> {
                Assertions.assertEquals(3, strategyFineline.getRfaStatusCode());
                Assertions.assertEquals(1, strategyFineline.getAllocTypeCode());
                Assertions.assertEquals(0, strategyFineline.getRunStatusCode());
            });
        }
    }



}
