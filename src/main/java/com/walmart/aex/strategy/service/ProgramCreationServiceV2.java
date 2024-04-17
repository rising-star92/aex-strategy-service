package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.config.RestTemplateConfig;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.properties.HttpConnectionProperties;
import com.walmart.aex.strategy.properties.RFAGraphQLProperties;
import com.walmart.aex.strategy.repository.ProgramCSARepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static graphql.Assert.assertTrue;

@Service
@Slf4j
public class ProgramCreationServiceV2 {

    private final DocumentStorageService documentStorageService;

    private final ProgramCSARepository programCSARepository;

    private final ExecutorService executorService;

    private final PlanDefinitionCallService planDefinitionCallService;

    ProgramCreationServiceV2(DocumentStorageService documentStorageService, ProgramCSARepository programCSARepository, ExecutorService executorService,
                             PlanDefinitionCallService planDefinitionCallService) {
        this.documentStorageService = documentStorageService;
        this.programCSARepository = programCSARepository;
        this.executorService = executorService;
        this.planDefinitionCallService = planDefinitionCallService;
    }

    //TODO: Update CCM Prod to exclude this API
    private static void populateIdsForProgram(Map<String, ProgramDTO> modsFromExcel, CSASeasonInfo csaSeasonInfo, HashMap<String, Integer> alreadyPresentPrograms, HashSet<ProgramDTO> programDTOS, List<Mod> mods) {
        modsFromExcel.values().forEach(programDTO -> {
            Integer programId = alreadyPresentPrograms.get(programDTO.getProgramName().toLowerCase()); //Used to determine if Program already Exists
            Integer csaId = programDTOS.stream().filter(programDTO1 -> programDTO1.getCsaSpaceInboundId().equals(csaSeasonInfo.getCsaId()) && programDTO1.getProgramId().equals(programId)).findFirst().orElse(new ProgramDTO()).getCsaSpaceInboundId(); //Used to determine if Program needs to be deleted
            programDTO.setCsaSpaceInboundId(csaId);
            programDTO.setProgramId(programId);
            programDTO.getMods().forEach(mod -> {
                mod.setProgramId(programId); //Used to filter the Mods the need to be created
                mod.setUniqueKey(mods.stream().filter(mod1 -> (csaId + "-" + mod.getModId() + "-" + programId).equals(mod1.getUniqueKey())).findAny().orElse(new Mod()).getUniqueKey());
            });
        });
    }

    @Transactional
    public List<ProgramDTOV2> generateProgramsV2(GenerateProgramsDTO generateProgramsDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, ProgramDTO>> modsFromExcelFuture = CompletableFuture.supplyAsync(() -> documentStorageService.fetchWorkbook(generateProgramsDTO.getFileName(), generateProgramsDTO.getDeptNbr()), executorService);
        CompletableFuture<CSASeasonInfo> csaSeasonInfoFuture = CompletableFuture.supplyAsync(() -> programCSARepository.fetchOrPersistCSAId(generateProgramsDTO), executorService);
        CompletableFuture<HashMap<String, Integer>> alreadyPresentProgramsFuture = CompletableFuture.supplyAsync(() -> programCSARepository.findAllProgramIdsAndNames(generateProgramsDTO.getDeptNbr()), executorService);
        CompletableFuture<HashSet<ProgramDTO>> programDTOSFuture = CompletableFuture.supplyAsync(() -> programCSARepository.findAllCSAIdsProgramIdsAndNames(generateProgramsDTO.getDeptNbr()), executorService);
        CompletableFuture<List<Mod>> modsFuture = CompletableFuture.supplyAsync(() -> programCSARepository.findAllMods(generateProgramsDTO.getDeptNbr()), executorService);
        CompletableFuture<List<Integer>> planIdsFuture = CompletableFuture.supplyAsync(() ->  planDefinitionCallService.getAexPlansGettingImpacted(generateProgramsDTO.getDeptNbr(), generateProgramsDTO.getSeasonStartWk(), generateProgramsDTO.getSeasonEndWk()));
        CompletableFuture<Void> result = CompletableFuture.allOf(modsFromExcelFuture, csaSeasonInfoFuture, alreadyPresentProgramsFuture, programDTOSFuture, modsFuture, planIdsFuture);
        result.get();
        assertTrue(modsFromExcelFuture.isDone());
        return processExcel(modsFromExcelFuture.get(), csaSeasonInfoFuture.get(), alreadyPresentProgramsFuture.get(), programDTOSFuture.get(), modsFuture.get(), generateProgramsDTO, planIdsFuture.get());
    }

    public List<ProgramDTOV2> processExcel(Map<String, ProgramDTO> modsFromExcel, CSASeasonInfo csaSeasonInfo, HashMap<String, Integer> alreadyPresentPrograms, HashSet<ProgramDTO> programDTOS, List<Mod> mods, GenerateProgramsDTO generateProgramsDTO, List<Integer> planIds) throws ExecutionException, InterruptedException {
        populateIdsForProgram(modsFromExcel, csaSeasonInfo, alreadyPresentPrograms, programDTOS, mods);
        CompletableFuture<Boolean> saves = CompletableFuture.supplyAsync(() -> programCSARepository.saveAll(modsFromExcel.values(), csaSeasonInfo.getCsaId(), generateProgramsDTO.getDeptNbr()));
        CompletableFuture<Boolean> updates = CompletableFuture.supplyAsync(() -> programCSARepository.updateCSASeasonStartAndEnd(csaSeasonInfo, generateProgramsDTO));
        CompletableFuture<Boolean> deletes = CompletableFuture.supplyAsync(() -> programCSARepository.delete(modsFromExcel, csaSeasonInfo.getCsaId(), programDTOS, planIds));
        CompletableFuture.allOf(saves, updates, deletes).get();
        return modsFromExcel.values().stream().map(programDTO -> new ProgramDTOV2(programDTO.getProgramId(), programDTO.getProgramName())).collect(Collectors.toList());

    }


}
