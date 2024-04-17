package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.customstore.CustomStoreRequest;
import com.walmart.aex.strategy.dto.programfixture.*;
import com.walmart.aex.strategy.dto.storecluster.Cluster;
import com.walmart.aex.strategy.dto.storecluster.StoreCluster;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterSearchRequest;
import com.walmart.aex.strategy.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CustomStoreService {

    private final ExecutorService executorService;
    private final ProgramFixtureService programFixtureService;
    private final StoreClusterService storeClusterService;

    public CustomStoreService(ExecutorService executorService, ProgramFixtureService programFixtureService,
                              StoreClusterService storeClusterService) {
        this.executorService = executorService;
        this.programFixtureService = programFixtureService;
        this.storeClusterService = storeClusterService;
    }

    private static final List<String> instructions = List.of(
            "Add columns to the Modular sheet for each custom store list",
            "Provide a name for the custom Group, make sure the name is unique to the list",
            "Store Nbr part of the list should always be from the existing list",
            "Make sure store Nbr is not repeated in another custom store list"
    );

    public ByteArrayInputStream fetchCustomStoreExcel(CustomStoreRequest request) {

        CompletableFuture<Map<String, List<Integer>>> storesMap = CompletableFuture.supplyAsync(() ->
                fetchStoresList(request), executorService);

        CompletableFuture<Map<String, List<Integer>>> storeClustersMap = CompletableFuture.supplyAsync(() ->
                fetchStoreClusters(request), executorService);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet instructionsSheet = workbook.createSheet("Instructions");
            populateInstructions(instructionsSheet);

            XSSFSheet customStoreSheet = workbook.createSheet(request.getProgramName());
            populateCustomStores(customStoreSheet, Stream.concat(storesMap.get().entrySet().stream(),
                            storeClustersMap.get().entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, LinkedHashMap::new)));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException exp) {
            log.error("IO Exception Occurred when generating Custom Store Excel - {}", exp.getMessage());
            throw new CustomException("Error Occurred when generating Custom Store Excel");
        } catch (ExecutionException exp) {
            log.error("Execution Exception Occurred when generating Custom Store Excel - {}", exp.getMessage());
            throw new CustomException(exp);
        } catch (InterruptedException exp) {
            log.error("Interrupted Exception Occurred when generating Custom Store Excel - {}", exp.getMessage());
            Thread.currentThread().interrupt();
            throw new CustomException(exp);
        }

    }

    private void populateCustomStores(XSSFSheet customStoreSheet, Map<String, List<Integer>> storeClusters) {

        int cellNum = 0;
        for (Map.Entry<String, List<Integer>> storeCluster : storeClusters.entrySet()) {
            int rowNum = 0;
            Row headerRow = customStoreSheet.getRow(rowNum) != null ? customStoreSheet.getRow(rowNum)
                    : customStoreSheet.createRow(rowNum);

            Cell headerCell = headerRow.createCell(cellNum);
            headerCell.setCellValue(storeCluster.getKey());

            for (Integer storeNum : storeCluster.getValue()) {
                rowNum++;
                Row row = customStoreSheet.getRow(rowNum) != null ? customStoreSheet.getRow(rowNum)
                        : customStoreSheet.createRow(rowNum);
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(storeNum);
            }
            cellNum++;
        }

    }

    private void populateInstructions(XSSFSheet instructionsSheet) {

        Row headerRow = instructionsSheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Instructions");

        int rowNum = 2;
        for (String instruction : instructions) {
            Row row = instructionsSheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(instruction);
        }

    }

    private Map<String, List<Integer>> fetchStoreClusters(CustomStoreRequest request) {
        return Optional.ofNullable(storeClusterService.fetchStoreClusters(createStoreClusterSearchRequest(request)))
                .map(storeClusters -> storeClusters
                        .stream().findFirst().map(StoreCluster::getClusters)
                        .stream().flatMap(Collection::stream)
                        .collect(Collectors.toMap(Cluster::getClusterName, Cluster::getStoreList)))
                .orElse(new HashMap<>());
    }

    private Map<String, List<Integer>> fetchStoresList(CustomStoreRequest request) {
        Map<String, List<Integer>> storeMap = new HashMap<>();
        storeMap.put("Store #", Optional.ofNullable(programFixtureService
                        .fetchProgramFixtures(createProgramFixtureRequest(request)))
                .map(programFixtures -> programFixtures
                        .getProgramDetails().stream()
                        .filter(programDetail -> request.getProgramName().equals(programDetail.getProgramName()))
                        .findFirst().map(ProgramDetail::getFixtures)
                        .stream().flatMap(Collection::stream)
                        .map(Fixture::getStoreList)
                        .flatMap(Collection::stream)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())).orElse(new ArrayList<>()));
        return storeMap;
    }

    private StoreClusterSearchRequest createStoreClusterSearchRequest(CustomStoreRequest request) {
        StoreClusterSearchRequest storeClusterSearchRequest = new StoreClusterSearchRequest();
        storeClusterSearchRequest.setEventId(request.getPlanId() + "_" + request.getProgramName());
        storeClusterSearchRequest.setAppName("aex-fashion");
        storeClusterSearchRequest.setClusterType("program");
        return storeClusterSearchRequest;
    }

    private ProgramFixtureRequest createProgramFixtureRequest(CustomStoreRequest request) {
        ProgramFixtureRequest programFixtureRequest = new ProgramFixtureRequest();
        programFixtureRequest.setDeptNumber(request.getDeptNbr());
        programFixtureRequest.setStartYrWeek(request.getStartWeek());
        programFixtureRequest.setEndYrWeek(request.getEndWeek());

        MerchantAllocationsDataRequest merchantAllocationsDataRequest = new MerchantAllocationsDataRequest();
        merchantAllocationsDataRequest.setMerchantName(request.getPrimaryMerchantName());
        programFixtureRequest.setMerchantAllocationsDataRequest(merchantAllocationsDataRequest);

        return programFixtureRequest;
    }

}
