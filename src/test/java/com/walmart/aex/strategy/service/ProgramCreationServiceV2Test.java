package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.repository.ProgramCSARepositoryImplTest;
import com.walmart.aex.strategy.repository.impl.ProgramCSARepositoryImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
class ProgramCreationServiceV2Test {

    DocumentStorageService documentStorageService;

    ProgramCSARepositoryImpl programCSARepository;
    ExecutorService executorService;
    ProgramCreationServiceV2 programCreationServiceV2;

    PlanDefinitionCallService planDefinitionCallService;
    @BeforeEach
    public void SetUp() {
        documentStorageService = Mockito.mock(DocumentStorageService.class);
        programCSARepository = Mockito.mock(ProgramCSARepositoryImpl.class);
        planDefinitionCallService = Mockito.mock(PlanDefinitionCallService.class);
        executorService = Executors.newFixedThreadPool(5);
        programCreationServiceV2 = new ProgramCreationServiceV2(documentStorageService,programCSARepository,executorService, planDefinitionCallService);
    }

    @Test
    void mockValues() throws ExecutionException, InterruptedException {
        List<Mod> mods = new ArrayList<>();
        List<ProgramDTO> programDTOS = ProgramCSARepositoryImplTest.fetchPrograms(50,1);
        programDTOS.forEach(programDTO -> mods.addAll(programDTO.getMods()));

        Map<String, ProgramDTO> programDTOMap = fetchTestResult(programDTOS);
        HashMap<String, Integer> programList = fetchProgramList(programDTOMap);
        CSASeasonInfo csaSeasonInfo = new CSASeasonInfo(1,202501,202530, false);
        List<Integer> planIds = Arrays.asList(1,2);
        GenerateProgramsDTO generateProgramsDTO = ProgramCSARepositoryImplTest.getInput(202510,202530);
        Mockito.when(documentStorageService.fetchWorkbook(anyString(),anyInt())).thenReturn(programDTOMap);
        Mockito.when(programCSARepository.fetchOrPersistCSAId(any())).thenReturn(csaSeasonInfo);
        Mockito.when(programCSARepository.findAllProgramIdsAndNames(anyInt())).thenReturn(programList);
        Mockito.when(programCSARepository.findAllCSAIdsProgramIdsAndNames(anyInt())).thenReturn(new HashSet<>(programDTOS));
        Mockito.when(programCSARepository.findAllMods(anyInt())).thenReturn(mods);
        Mockito.when(planDefinitionCallService.getAexPlansGettingImpacted(anyInt(),anyInt(),anyInt())).thenReturn(planIds);

        List<ProgramDTOV2> result = programCreationServiceV2.generateProgramsV2(generateProgramsDTO);
        Assert.assertEquals(result.size(),programDTOS.size());

    }

    private HashMap<String, Integer> fetchProgramList(Map<String, ProgramDTO> programDTOMap) {
        HashMap<String, Integer> update = new HashMap<>();
        programDTOMap.values().forEach(programDTO ->{
            update.put(programDTO.getProgramName().toLowerCase(),programDTO.getProgramId());
        });
        return update;
    }

    private Map<String, ProgramDTO> fetchTestResult(List<ProgramDTO> programDTOS) {
        Map<String,ProgramDTO> programDTOMap = new HashMap<>();
        programDTOS.forEach(programDTO -> {
            programDTOMap.put(programDTO.getProgramName().toLowerCase(),programDTO);
        });
        return programDTOMap;
    }

}
