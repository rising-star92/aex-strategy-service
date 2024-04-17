package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.CSASeasonInfo;
import com.walmart.aex.strategy.dto.GenerateProgramsDTO;
import com.walmart.aex.strategy.dto.Mod;
import com.walmart.aex.strategy.dto.ProgramDTO;

import java.util.*;

public interface ProgramCSARepository {
    boolean updateCSASeasonStartAndEnd(CSASeasonInfo csaSeasonInfo,GenerateProgramsDTO generateProgramsDTO);

    boolean saveAll(Collection<ProgramDTO> programs, int csaId, int deptNbr);

    HashMap<String, Integer> findAllProgramIdsAndNames(int deptNbr);

    HashSet<ProgramDTO> findAllCSAIdsProgramIdsAndNames(int deptNbr);

    List<Mod> findAllMods(int deptNbr);

    CSASeasonInfo fetchOrPersistCSAId(GenerateProgramsDTO generateProgramsDTO);

    boolean delete(Map<String, ProgramDTO> modsFromExcel, Integer csaId, HashSet<ProgramDTO> programDTOS, List<Integer> planIds);
}
