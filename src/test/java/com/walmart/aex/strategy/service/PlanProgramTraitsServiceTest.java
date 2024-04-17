package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.CategoryTraitProgram;
import com.walmart.aex.strategy.dto.CsaReservedProgramsResponse;
import com.walmart.aex.strategy.dto.ProgramResponseDTO;
import com.walmart.aex.strategy.dto.ProgramTraitsResponse;
import com.walmart.aex.strategy.entity.Program;
import com.walmart.aex.strategy.entity.StrategyFlClusPrgmEligRanking;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.CategoryTraitProgramsRepository;
import com.walmart.aex.strategy.repository.ProgramRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import com.walmart.aex.strategy.util.Constant;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanProgramTraitsServiceTest {
    @InjectMocks
    private PlanProgramTraitsService planProgramTraitsService;

    @Mock
    private CategoryTraitProgramsRepository categoryTraitProgramsRepository;

    @Mock
    private StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper;

    private HttpEntity fetchEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("wm_svc.name", "PLAN-PROGRAM-TRAITS-SERVICE");
        headers.set("wm_svc.env", "dev");
        return new HttpEntity(headers);
    }

    private List<Program> getPrograms(){
        Program program = new Program();
        program.setProgramId(643);
        program.setUserId(Constant.AEX_STRATEGY_SERVICE);
        program.setDeptNbr(23);
        List<Program> programs = new ArrayList<>();
        programs.add(program);
        return programs;
    }

    @Test
    void testDeleteProgramEligibilityById() {
        Long programId = 1L;
        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = new StrategyFlClusPrgmEligRanking();
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = new HashSet<>();
        strategyFlClusPrgmEligRanking.setStoreCount(100);
        strategyFlClusPrgmEligRankings.add(strategyFlClusPrgmEligRanking);
        when(strategyFlClusPrgmEligRankingRepository.findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_programId(programId)).thenReturn(Optional.of(strategyFlClusPrgmEligRankings));
        doNothing().when(strategyFlClusPrgmEligRankingRepository).deleteStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_programId(programId);
        ProgramResponseDTO result =  planProgramTraitsService.deleteProgramEligibilityByProgramId(programId);
        Assertions.assertEquals("Success", result.getStatus());
    }

    @Nested
    @DisplayName("Testing Trait Programs Fetch")
    class TestTraitPrograms{

        List<String> planIds = new ArrayList<>(Arrays.asList("386", "387"));
        AppProperties appProperties;
        @BeforeEach
        void setDataForRun() throws IOException {


            appProperties = PowerMockito.mock(AppProperties.class);
            PowerMockito.when(appProperties.getCSAUrl()).thenReturn("https://localhost:8080");
            PowerMockito.when(appProperties.getCSAEnv()).thenReturn("dev");
            PowerMockito.when(appProperties.getCsaProgramsPlanId()).thenReturn(planIds);

            ReflectionTestUtils.setField(planProgramTraitsService, "appProperties", appProperties);

            List<CategoryTraitProgram> list= new ArrayList<>();
            list.add(new CategoryTraitProgram(23,2668,202301,202302));
            list.add(new CategoryTraitProgram(23,12234,202301,202302));
            when(categoryTraitProgramsRepository.getCategoryTraitPrograms(anyLong())).thenReturn(list);

            Map<String,String> params = new HashMap<>();
            params.put("deptId", String.valueOf(23));
            params.put("startYrWeek", String.valueOf(202301));
            params.put("endYrWeek", String.valueOf(202302));

            String json = new String(Files.readAllBytes(Paths.get("src/test/resources/csaResponse.json")));
            CsaReservedProgramsResponse [] body = objectMapper.readValue(json, CsaReservedProgramsResponse[].class);
            Mockito.when(restTemplate.exchange("https://localhost:8080?deptId={deptId}&startYrWeek={startYrWeek}&endYrWeek={endYrWeek}", HttpMethod.GET, fetchEntity(), CsaReservedProgramsResponse[].class,params)).thenReturn(ResponseEntity.ok(body));
        }

        @Test
        void testGetPlanProgramTraits_Filtering_Programs() {
            Program program = new Program();
            program.setProgramId(643);
            program.setUserId(Constant.AEX_STRATEGY_SERVICE);
            program.setDeptNbr(23);
            List<Program> programs = new ArrayList<>();
            programs.add(program);
            when(programRepository.getPrograms_By_DeptNbr(Constant.AEX_STRATEGY_SERVICE, 23)).thenReturn(programs);

            ProgramTraitsResponse result = planProgramTraitsService.getPlanProgramTraits(389L);
            Assertions.assertEquals(1, result.getTrait().size());
            Assertions.assertEquals(643, result.getTrait().stream().findFirst().get().getProgramId());
        }

        @Test
        void testGetPlanProgramTraits_And_Return_CSA_Programs_For_Old_Plans() {
            Program program = new Program();
            program.setProgramId(643);
            program.setUserId(Constant.AEX_STRATEGY_SERVICE);
            program.setDeptNbr(23);
            List<Program> programs = new ArrayList<>();
            programs.add(program);
            when(programRepository.getPrograms_By_DeptNbr(Constant.AEX_STRATEGY_SERVICE, 23)).thenReturn(programs);

            ProgramTraitsResponse result = planProgramTraitsService.getPlanProgramTraits(387L);
            Assertions.assertEquals(2, result.getTrait().size());
        }

        @Test
        void testGetPlanProgramTraits_And_Return_CSA_Programs_If_Strategy_Programs_Empty() {
            List<Program> programs = new ArrayList<>();
            when(programRepository.getPrograms_By_DeptNbr(Constant.AEX_STRATEGY_SERVICE, 23)).thenReturn(programs);

            ProgramTraitsResponse result = planProgramTraitsService.getPlanProgramTraits(389L);
            Assertions.assertEquals(3, result.getTrait().size());
        }
    }
}
