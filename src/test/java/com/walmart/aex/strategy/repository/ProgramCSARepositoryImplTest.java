package com.walmart.aex.strategy.repository;


import com.walmart.aex.strategy.dto.CSASeasonInfo;
import com.walmart.aex.strategy.dto.GenerateProgramsDTO;
import com.walmart.aex.strategy.dto.Mod;
import com.walmart.aex.strategy.dto.ProgramDTO;
import com.walmart.aex.strategy.repository.impl.ProgramCSARepositoryImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@JdbcTest
@Sql({"/program.schema.sql"})
public class ProgramCSARepositoryImplTest {
    @Autowired
    private JdbcTemplate strategyJdbcTemplate;

    @NotNull
    public static GenerateProgramsDTO getInput(int start, int end) {
        return new GenerateProgramsDTO("csaSpaceFile", start, end, 2025, 2323, "H1", 23);
    }

    @Test
    public void testInsertProgram(){
        ProgramCSARepository programCSARepository = new ProgramCSARepositoryImpl(strategyJdbcTemplate, strategyJdbcTemplate.getDataSource());
        GenerateProgramsDTO input = getInput(202515,202525);
        //Check if the fetch works
        CSASeasonInfo csaSeasonInfo = programCSARepository.fetchOrPersistCSAId(input);
        Assert.assertNotNull(csaSeasonInfo);
        Assert.assertEquals((int) csaSeasonInfo.getSeasonEndWk(), input.getSeasonEndWk());
        Assert.assertEquals((int) csaSeasonInfo.getSeasonStartWk(), input.getSeasonStartWk());
        Assert.assertEquals((int) csaSeasonInfo.getCsaId(), 1);

        programCSARepository.updateCSASeasonStartAndEnd(csaSeasonInfo, getInput(202520,202530));

        csaSeasonInfo = programCSARepository.fetchOrPersistCSAId(input);
        Assert.assertEquals((int) csaSeasonInfo.getSeasonStartWk(), 202520);
        Assert.assertEquals((int) csaSeasonInfo.getSeasonEndWk(), 202530);

        List<ProgramDTO> programDTOS = fetchPrograms(5, null);
        programCSARepository.saveAll(programDTOS,csaSeasonInfo.getCsaId(),23);

        //Assert.assertEquals(5,programCSARepository.fetchMaxProgramId(23).intValue());
        HashSet<ProgramDTO> programDTOSet = programCSARepository.findAllCSAIdsProgramIdsAndNames(23);
        Assert.assertEquals(5,programDTOSet.size());

        Map<String,Integer> map = programCSARepository.findAllProgramIdsAndNames(23);
        Assert.assertEquals(5,map.size());

        List<Mod> mods = programCSARepository.findAllMods(23);
        Assert.assertEquals(5,mods.size());
        Map<String,ProgramDTO> inputDelete = new HashMap<>();



        programCSARepository.delete(inputDelete, csaSeasonInfo.getCsaId(), programDTOSet, new ArrayList<>());

        map = programCSARepository.findAllProgramIdsAndNames(23);
        Assert.assertEquals(0,map.size());

        mods = programCSARepository.findAllMods(23);
        Assert.assertEquals(0,mods.size());

    }

    public static List<ProgramDTO> fetchPrograms(int size, Integer csaId) {
        List<ProgramDTO> result = new ArrayList<>();
        for(int i =0; i < size; i++){
            int modId = i*10;
            String programName = "program Name" + i;
            Mod mod = new Mod("Mod Name", csaId + "-" + i + "-"+ modId,modId,null,i,programName,"Mandy","Brand",23, Arrays.asList("RACK"));
            result.add(new ProgramDTO(i,csaId,programName, null, List.of(mod)));
        }
        return result;
    }

}
