package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.Mod;
import com.walmart.aex.strategy.dto.ProgramDTO;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.ProgramRepository;
import com.walmart.aex.strategy.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProgramPersistService {

	private final EntityManager entityManager;

	private final ProgramRepository programRepository;



	ProgramPersistService(EntityManager entityManager, ProgramRepository programRepository){
		this.entityManager = entityManager;
		this.programRepository = programRepository;
	}


	@Async
	@Transactional
	public CompletableFuture<Integer> persistProgram(LocalDate startSeason, LocalDate endSeason, Program program, ProgramDTO programDTO) {
		entityManager.persist(program);
		List<ProgramStore> programStores = new ArrayList<>();
		programDTO.getStoreList().forEach(storeId ->{
			ProgramStore programStore = getProgramStore(startSeason, endSeason, program, (long) storeId);
			programStores.add(programStore);
		});
		List<ProgramModCat> modCats = new ArrayList<>();
		programDTO.getMods().forEach(mod ->{
			ProgramModCat modCat = getModCats(startSeason, program,  mod);
			modCats.add(modCat);
		});
		modCats.forEach(entityManager::persist);
		programStores.forEach(entityManager::persist);

		return CompletableFuture.completedFuture(program.getProgramId());
	}

	@NotNull
	public static Program createProgram(String programName, LocalDate startSeason, Integer deptNbr) {
		Program program = new Program();
		program.setProgramName(programName);
		program.setTraitNbr(-1);
		program.setDeptNbr(deptNbr);
		program.setAcctDeptNbr(deptNbr);
		program.setLongDesc(programName);
		program.setUpdateDate(startSeason);
		program.setCreateDate(startSeason);
		program.setUserId(Constant.AEX_STRATEGY_SERVICE);
		program.setShortDesc(programName.substring(0, Math.min(programName.length(), 59)));
		return program;
	}

	@NotNull
	public static ProgramModCat getModCats(LocalDate startSeason, Program program, Mod mod) {
		ProgramModCat cur = new ProgramModCat();
		cur.setProgramModCatId(new ProgramModCatId(program.getProgramId(),mod.getDeptNbr(),mod.getModId()));
		cur.setModCatDesc(mod.getModName());
		cur.setProgram(program);
		cur.setFirstName(mod.getFirstName());
		cur.setLastName(mod.getLastName());
		cur.setUpdateDate(startSeason);
		cur.setCreateDate(startSeason);
		cur.setCreateUserId(Constant.AEX_STRATEGY_SERVICE);
		cur.setLastModifiedUserId(Constant.AEX_STRATEGY_SERVICE);
		return cur;
	}

	@NotNull
	public static ProgramStore getProgramStore(LocalDate startSeason, LocalDate endSeason, Program program, long storeId) {
		ProgramStore programStore = new ProgramStore();
		programStore.setProgram(program);
		programStore.setStartDate(startSeason);
		programStore.setEndDate(endSeason);
		programStore.setBuId(-1);
		ProgramStoreId programStoreId = new ProgramStoreId();
		int programId =  Optional.ofNullable(program.getProgramId()).orElse(0);
		programStoreId.setProgramId((long) programId);
		programStoreId.setStoreNbr(storeId);
		programStore.setProgramStoreId(programStoreId);
		return programStore;
	}

	@Async
	@Transactional
	public CompletableFuture<Integer> updateProgram(LocalDate startSeason, LocalDate endSeason, ProgramDTO programDTO) {
		Program program = programRepository.findById(programDTO.getProgramId()).orElseThrow(()-> new CustomException("Program not found: ProgramId : " + programDTO.getProgramId()));
		boolean checkIfUpdateRequiredStore = checkUpdateStores(programDTO,program);
		boolean checkIfUpdateRequiredMod = checkUpdateMod(programDTO,program);

		if(checkIfUpdateRequiredStore) {
			Set<ProgramStore> programStores = new HashSet<>();
			programDTO.getStoreList().forEach(storeId -> programStores.add(ProgramPersistService.getProgramStore(startSeason, endSeason, program, (long) storeId)));
			program.setProgramStores(programStores);
			program.setUpdateDate(LocalDate.now());
			entityManager.merge(program);
		}

		if(checkIfUpdateRequiredMod) {
			Set<ProgramModCat> modCats = new HashSet<>();
			programDTO.getMods().forEach(mod -> modCats.add(ProgramPersistService.getModCats(startSeason, program,  mod)));
			program.setProgramModCats(modCats);
			program.setUpdateDate(LocalDate.now());
			entityManager.merge(program);
		}
		return CompletableFuture.completedFuture(program.getProgramId());
	}

	private boolean checkUpdateStores(ProgramDTO programDTO, Program program) {
		Set<Integer> storeListDB = program.getProgramStores().stream().map(store->store.getProgramStoreId().getStoreNbr().intValue()).collect(Collectors.toSet());
		Set<Integer> storeListExcel = programDTO.getStoreList();
		return !storeListExcel.equals(storeListDB);
	}

	private boolean checkUpdateMod(ProgramDTO programDTO, Program program) {
		Set<Integer> modIdsDB = program.getProgramModCats().stream().map(modCat->modCat.getProgramModCatId().getModCatNbr()).collect(Collectors.toSet());
		Set<Integer> modIdsExcel = programDTO.getMods().stream().map(Mod::getModId).collect(Collectors.toSet());

		Set<String> modNamesDB = program.getProgramModCats().stream().map(ProgramModCat::getModCatDesc).collect(Collectors.toSet());
		Set<String> modNamesExcel = programDTO.getMods().stream().map(Mod::getModName).collect(Collectors.toSet());
		return !modIdsExcel.equals(modIdsDB) || !modNamesExcel.equals(modNamesDB);
	}



}
