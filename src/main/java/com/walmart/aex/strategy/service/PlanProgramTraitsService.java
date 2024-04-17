package com.walmart.aex.strategy.service;

import java.util.*;
import java.util.stream.Collectors;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.entity.Program;
import com.walmart.aex.strategy.repository.ProgramRepository;
import com.walmart.aex.strategy.util.Constant;
import org.springframework.util.CollectionUtils;
import com.walmart.aex.strategy.entity.StrategyFlClusPrgmEligRanking;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.walmart.aex.strategy.repository.CategoryTraitProgramsRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class PlanProgramTraitsService {


    private CategoryTraitProgramsRepository categoryTraitProgramsRepository;
    private final StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;
    private final RestTemplate restTemplate;

    private final ProgramRepository programRepository;

    @ManagedConfiguration
    private AppProperties appProperties;

    public PlanProgramTraitsService(CategoryTraitProgramsRepository categoryTraitProgramsRepository,
                                    @Qualifier("APRestTemplate") RestTemplate restTemplate,
                                    StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository,
                                    ProgramRepository programRepository) {
        this.categoryTraitProgramsRepository = categoryTraitProgramsRepository;
        this.restTemplate = restTemplate;
        this.strategyFlClusPrgmEligRankingRepository = strategyFlClusPrgmEligRankingRepository;
        this.programRepository = programRepository;
    }

    public ProgramTraitsResponse getPlanProgramTraits(Long planId) {
        try {
            List<CategoryTraitProgram> programs = categoryTraitProgramsRepository.getCategoryTraitPrograms(planId);
            int start = programs.stream().mapToInt(CategoryTraitProgram::getStartYrWeek).min().orElseThrow(()-> new CustomException("No data found."));
            int end = programs.stream().mapToInt(CategoryTraitProgram::getEndYrWeek).max().orElseThrow(()-> new CustomException("No data found."));
            int deptId = programs.get(0).getDeptId();
            List<String> categories = programs.stream().map(CategoryTraitProgram::getCatgId).map(String::valueOf).distinct()
                    .collect(Collectors.toList());
            List<Trait> traits = new ArrayList<>();
            List<String> planIds = new ArrayList<>(appProperties.getCsaProgramsPlanId());
            boolean fetchOnlyOldPrgms = planIds.contains(planId.toString());
            if (Boolean.parseBoolean(appProperties.getAPS4ReleaseFlag())) {
                List<String> spaceTypeList = new ArrayList<>(appProperties.getCsaSpaceType());
                for(String spaceType : spaceTypeList){
                    List<CsaReservedProgramsResponse> response = callCsaService(deptId, start, end, spaceType);
                    traits.addAll(fetchMods(response, deptId, categories, fetchOnlyOldPrgms));
                }
            } else{
                List<CsaReservedProgramsResponse> response = callCsaService(deptId, start, end);
                traits = fetchMods(response, deptId, categories, fetchOnlyOldPrgms);
            }


            return new ProgramTraitsResponse(traits);
        } catch (HttpClientErrorException e) {
            throw new CustomException("Seasons not found []");
        } catch (Exception e) {
            log.error("Error fetching Category Trait Program from repository or parsing response form repository for planId: {}, exp:{} ", planId, e.toString());
            throw e;
        }
    }

    @Retryable(exclude = HttpClientErrorException.BadRequest.class, backoff = @Backoff(delay = 10000))
    private List<CsaReservedProgramsResponse> callCsaService(int deptId, int startYrWeek, int endYrWeek, String spaceType) {
        List<CsaReservedProgramsResponse> response = new ArrayList<>();
        HttpHeaders headers = fetchHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(appProperties.getCSAUrl())
                .queryParam("deptId", "{deptId}")
                .queryParam("startYrWeek", "{startYrWeek}")
                .queryParam("endYrWeek", "{endYrWeek}")
                .queryParam("spaceType", "{spaceType}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("deptId", String.valueOf(deptId));
        params.put("startYrWeek", String.valueOf(startYrWeek));
        params.put("endYrWeek", String.valueOf(endYrWeek));
        params.put("spaceType", spaceType);
        try {
            ResponseEntity<CsaReservedProgramsResponse[]> responseEntity = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    CsaReservedProgramsResponse[].class, params);
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                response = Arrays.asList(responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching value from CSA, exp:{} ", e.toString());
            throw e;
        }
        return response;

    }

    @Retryable(exclude = HttpClientErrorException.BadRequest.class, backoff = @Backoff(delay = 10000))
    private List<CsaReservedProgramsResponse> callCsaService(int deptId, int startYrWeek, int endYrWeek) {
        List<CsaReservedProgramsResponse> response = new ArrayList<>();
        HttpHeaders headers = fetchHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(appProperties.getCSAUrl())
                .queryParam("deptId", "{deptId}")
                .queryParam("startYrWeek", "{startYrWeek}")
                .queryParam("endYrWeek", "{endYrWeek}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("deptId", String.valueOf(deptId));
        params.put("startYrWeek", String.valueOf(startYrWeek));
        params.put("endYrWeek", String.valueOf(endYrWeek));
        try {
            ResponseEntity<CsaReservedProgramsResponse[]> responseEntity = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    CsaReservedProgramsResponse[].class, params);
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                response = Arrays.asList(responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching value from CSA, exp:{} ", e.toString());
            throw e;
        }
        return response;

    }

    @Recover
    public String recover(Exception e, Long planId, HttpMethod httpMethod) {
        throw new CustomException("TimeOut getting response from CSA API: {} " + planId + e.toString());
    }

    private HttpHeaders fetchHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("wm_svc.name", "PLAN-PROGRAM-TRAITS-SERVICE");
        headers.set("wm_svc.env", appProperties.getCSAEnv());
        return headers;
    }

    @Transactional
    public ProgramResponseDTO deleteProgramEligibilityByProgramId(Long programId){
        ProgramResponseDTO programResponseDTO = new ProgramResponseDTO();
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings =
                strategyFlClusPrgmEligRankingRepository.findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_programId(programId).orElse(null);
        if(!CollectionUtils.isEmpty(strategyFlClusPrgmEligRankings)){
            strategyFlClusPrgmEligRankingRepository.deleteStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_programId(programId);
            ProgramMappingDTO programMappingDTO = new ProgramMappingDTO();
            programMappingDTO.setProgramId(programId);
            programResponseDTO.setProgramMappings(Collections.singletonList(programMappingDTO));
        }
        programResponseDTO.setStatus("Success");
        return programResponseDTO;
    }

    private List<Trait> fetchMods(List<CsaReservedProgramsResponse> csaPrograms, Integer deptNbr, List<String> categories, boolean fetchOnlyOldPrgms){
        log.info("Fetching category mods");
        List<Trait> traits;
        List<Program> programs = programRepository.getPrograms_By_DeptNbr(Constant.AEX_STRATEGY_SERVICE, deptNbr);
        if(!CollectionUtils.isEmpty(programs)) {
            List<Integer> programdIds = programs.stream().map(Program::getProgramId).collect(Collectors.toList());
            if(fetchOnlyOldPrgms){
                traits = fetchCSAProgramsByCategory(csaPrograms, categories);
                traits = traits.stream().filter(trait -> !programdIds.contains(trait.getProgramId())).collect(Collectors.toList());
            }else {
                traits = csaPrograms.stream().filter(o -> categories.contains(o.getCategoryId()))
                        .flatMap(o -> o.getProgramDetails().stream().filter(prgDetail -> programdIds.contains(prgDetail.getProgramId()))).distinct().collect(Collectors.toList());
            }
        }
        else{
            traits = fetchCSAProgramsByCategory(csaPrograms, categories);
        }
        return  traits;
    }

    private List<Trait> fetchCSAProgramsByCategory(List<CsaReservedProgramsResponse> csaPrograms, List<String> categories){
        return csaPrograms.stream().filter(o -> categories.contains(o.getCategoryId()))
                .flatMap(o -> o.getProgramDetails().stream()).distinct().collect(Collectors.toList());
    }
}
