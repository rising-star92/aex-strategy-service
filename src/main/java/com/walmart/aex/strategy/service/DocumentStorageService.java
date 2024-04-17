package com.walmart.aex.strategy.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.walmart.aex.strategy.dto.Mod;
import com.walmart.aex.strategy.dto.ProgramDTO;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.reader.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class DocumentStorageService {

	private final BlobContainerClient blobContainerClient;


	DocumentStorageService(BlobContainerClient blobContainerClient ){
		this.blobContainerClient = blobContainerClient;
	}


	public Map<String, ProgramDTO> fetchWorkbook(String fileName,int deptNbr) {
		try {
			BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
			if(!blobClient.exists()){
				throw new CustomException("File not found in Data store " + fileName);
			}
			BinaryData binaryData = blobClient.downloadContent();
			long begin =  System.currentTimeMillis();
			Map<String, ProgramDTO> result =  getGenerateModsFromExcel(binaryData.toStream(),deptNbr);
			long end =  System.currentTimeMillis();
			log.info("Time for Processing {}", end-begin);
			return result;

		} catch (IOException e) {
			log.error("Error Occurred while fetching the file from {} ", fileName,e);
			throw new CustomException("File not found in Data store " + fileName);
		}
	}

	private Map<String, ProgramDTO> getGenerateModsFromExcel(InputStream inputStream, int deptNbr) throws IOException {
		Map<String, ProgramDTO> modsFromExcel = new HashMap<>();

		final int colModCatNamesStarts = 5; //TODO: make generic the start Column value
		final int rowModCatNamesStarts = 5;
		final int rowMerchantNamesStarts = 0;


		try ( ReadableWorkbook wb = new ReadableWorkbook(inputStream)) {
			Sheet sheet = wb.getFirstSheet();
			try (Stream<Row> rows = sheet.openStream()) {
				List<Row> allRows = rows.filter(row->row.getCell(4).getAddress().getRow() <9).collect(Collectors.toList());
				Map<Integer, String> merchants = fetchFromRows(allRows,rowMerchantNamesStarts);

				Map<Integer, String> programs = fetchFromRows(allRows,rowModCatNamesStarts);
				Map<Integer, String> modsNames =fetchFromRows(allRows,rowModCatNamesStarts-1);
				Map<Integer, String> fixtureTypes =fetchFromRows(allRows,rowModCatNamesStarts+3);

				Map<Integer, Integer> modsId = allRows.get(rowModCatNamesStarts+2).stream().filter(val -> val !=null && val.getRawValue() != null && CommonUtil.isInteger(val.getRawValue())).collect(Collectors.toMap(val-> val.getAddress().getColumn(), val->Integer.parseInt(val.getRawValue())));

				programs.forEach((colNum,programName)->{
					if(colNum >= colModCatNamesStarts) {
						String[] merchant = merchants.get(colNum).split(" ");
						List<String> fixture = new ArrayList<>();
						fixture.add(fixtureTypes.get(colNum));
						Mod newMod = new Mod(modsNames.get(colNum), programName, modsId.get(colNum), null, null, programName, merchant[0], merchant[1], deptNbr,fixture);

						if (modsFromExcel.containsKey(programName.toLowerCase())) {
							ProgramDTO programDTO = modsFromExcel.get(programName.toLowerCase());
							List<Mod> mods = programDTO.getMods();
							AtomicBoolean alreadyPresent = new AtomicBoolean(false);
							mods.forEach(mod ->{
								if(mod.getModId().equals(newMod.getModId())){
									alreadyPresent.set(true);
									mod.getFixtureType().add(fixtureTypes.get(colNum));
								}
							});
							if(!alreadyPresent.get()){
								mods.add(newMod);
							}
						} else {
							List<Mod> newMods = new ArrayList<>();
							newMods.add(newMod);
							ProgramDTO programDTO = new ProgramDTO(null, programName, null, newMods);
							modsFromExcel.put(programName.toLowerCase(), programDTO);
						}
					}
				});
			}
		}
		return modsFromExcel;

	}

	private Map<Integer, String> fetchFromRows(List<Row> allRows, int index){
		return allRows.get(index).stream().filter(val -> val !=null && val.getRawValue() != null).collect(Collectors.toMap(val-> val.getAddress().getColumn(),Cell::getRawValue));
	}

}
