package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.Mod;
import com.walmart.aex.strategy.dto.ProgramDTO;
import com.walmart.aex.strategy.entity.Program;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.ProgramRepository;
import com.walmart.aex.strategy.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProgramCreationService {

	private final ProgramRepository programRepository;

	private final ProgramPersistService programPersistService;



	ProgramCreationService(ProgramRepository programRepository,ProgramPersistService programPersistService){
		this.programRepository = programRepository;
		this.programPersistService = programPersistService;
	}


	public InputStream generatePrograms(InputStream file) throws IOException {
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);
		LocalDate startSeason = fetchDateValueFromCell(sheet.getRow(1),1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endSeason = fetchDateValueFromCell(sheet.getRow(4),1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int deptNbr = fetchNumericValueFromCell(sheet.getRow(0),1);

		Map<String, ProgramDTO> modsFromExcel = getGenerateModsFromExcel(sheet);
		HashMap<Program,ProgramDTO> programModHashMap = new HashMap<>();

		List<Program> modsPresent = programRepository.programAlreadyPresent(modsFromExcel.keySet().stream().map(String::toString).collect(Collectors.toList()),startSeason , Constant.AEX_STRATEGY_SERVICE, deptNbr);
		modsFromExcel.values().forEach(programDTO-> modsPresent.forEach(program -> {
			if(programDTO.getProgramName().equalsIgnoreCase(program.getLongDesc())){
				programDTO.setProgramId(program.getProgramId());
			}
		}));
		List<CompletableFuture<Integer>> futures = new ArrayList<>();

		modsFromExcel.values().forEach(programDTO -> {
			boolean update = programDTO.getProgramId() != null;
				if(!update) { //Program is not present create a new one
					Program program = ProgramPersistService.createProgram(programDTO.getProgramName(), startSeason,deptNbr);
					programModHashMap.put(program, programDTO);
				} else { //update old program
					futures.add(programPersistService.updateProgram(startSeason,endSeason,programDTO));
				}
		});

		programModHashMap.forEach((program, programDTO) -> futures.add(programPersistService.persistProgram(startSeason, endSeason, program, programDTO)));


		List<Integer> programs = new ArrayList<>();
		futures.forEach(future -> {
			try {
				Integer result = future.get();
				if(result == null){
					throw new CustomException("Creation in DB failed.");
				} else {
					programs.add(result);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		return generateResponseExcel(programRepository.findAllById(programs));
	}

	@NotNull
	public static Map<String, ProgramDTO> getGenerateModsFromExcel(Sheet sheet) {
		Map<Integer,Integer> rowIndexToStore = new HashMap<>();
		Map<String, ProgramDTO> modsFromExcel = new HashMap<>();

		int deptNbr = fetchNumericValueFromCell(sheet.getRow(0),1);
		int rowStoreStarts = 8;
		int colStoreStarts = 0;
		int colModCatNamesStarts = 11;
		int rowModCatNamesStarts = 4;
		int rowMerchantNamesStarts = 0;

		//find stores to index on Sheet
		for (int i = rowStoreStarts; i < sheet.getLastRowNum() + 1; i++) {
			rowIndexToStore.put(i,fetchNumericValueFromCell(sheet.getRow(i), colStoreStarts));
		}

		//find Mod to index on Sheet
		for (int i = colModCatNamesStarts; i < sheet.getRow(rowModCatNamesStarts).getLastCellNum(); i++) {
			String programName = fetchStringValueFromCell(sheet.getRow(rowModCatNamesStarts + 1),i).trim();
			String modName = fetchStringValueFromCell(sheet.getRow(rowModCatNamesStarts),i).trim();
			String[] merchant = fetchStringValueFromCell(sheet.getRow(rowMerchantNamesStarts),i).trim().split(" ");
			String fixture = fetchStringValueFromCell(sheet.getRow(rowModCatNamesStarts + 3),i).trim();
			Integer modId = fetchNumericValueFromCell(sheet.getRow(rowModCatNamesStarts + 2),i);

			if(!programName.isEmpty()) {
				HashSet<Integer> storeList = new HashSet<>();
				for (int j = rowStoreStarts; j < sheet.getLastRowNum() + 1; j++) {
					int storeValid = fetchNumericValueFromCell(sheet.getRow(j),i);
					if(storeValid > 0){
						storeList.add(rowIndexToStore.get(j));
					}
				}
				Mod mod = new Mod(modName,programName,modId, storeList,null,programName, merchant[0],merchant[1],deptNbr);

				if(!modsFromExcel.containsKey(programName.toLowerCase())) {
					List<Mod> newMods = new ArrayList<>();
					newMods.add(mod);
					ProgramDTO programDTO = new ProgramDTO(null,programName,storeList,newMods);
					modsFromExcel.put(programName.toLowerCase(),programDTO);
				} else {
					List<Mod> curMods = modsFromExcel.get(programName.toLowerCase()).getMods();
					if (curMods.stream().noneMatch(curMod -> mod.getModId().equals(curMod.getModId()))){
						curMods.add(mod);
					}
					modsFromExcel.get(programName.toLowerCase()).getStoreList().addAll(storeList);
				}
			} else {
				throw new CustomException("Program Names cannot be null");
			}
		}

		return modsFromExcel;
	}


	@NotNull
	public static InputStream generateResponseExcel(List<Program> programs) throws IOException {
		Workbook workbookResponse = new XSSFWorkbook();

		Sheet sheetResponse = workbookResponse.createSheet("programs");
		Row header = sheetResponse.createRow(0);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("ProgramId");

		headerCell = header.createCell(1);
		headerCell.setCellValue("ProgramName");

		AtomicInteger i = new AtomicInteger(1);
		programs.forEach(program -> {
			Row row = sheetResponse.createRow(i.getAndIncrement());
			int programId =  Optional.ofNullable(program.getProgramId()).orElse(0);
			row.createCell(0).setCellValue(programId);
			row.createCell(1).setCellValue(program.getProgramName());
		});
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbookResponse.write(bos);
		byte[] array = bos.toByteArray();
		return new ByteArrayInputStream(array);
	}

	private static int fetchNumericValueFromCell(Row row,int index) {
		Cell cell = row.getCell(index);
		if(cell != null) {
			try {
				return (int) Math.ceil(cell.getNumericCellValue());
			} catch (Exception e){
				return 0;
			}
		} else {
			return 0;
		}
	}

	private static String fetchStringValueFromCell(Row row,int index) {
		if(row.getCell(index) != null) {
			return row.getCell(index).getStringCellValue().trim();
		} else {
			return "";
		}
	}

	private static Date fetchDateValueFromCell(Row row,int index) {
		return row.getCell(index).getDateCellValue();
	}

}
