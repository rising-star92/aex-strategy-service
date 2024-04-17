package com.walmart.aex.strategy.repository.impl;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.enums.FixtureTypeRollup;
import com.walmart.aex.strategy.repository.ProgramCSARepository;
import com.walmart.aex.strategy.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ProgramCSARepositoryImpl  implements ProgramCSARepository {
    private final JdbcTemplate strategyJdbcTemplate;

    private final DataSource dataSource;

    public ProgramCSARepositoryImpl(JdbcTemplate strategyJdbcTemplate,DataSource dataSource) {
        this.strategyJdbcTemplate = strategyJdbcTemplate;
        this.dataSource = dataSource;
    }


    @Override
    public List<Mod> findAllMods(int deptNbr) {
        return strategyJdbcTemplate.query(String.format("select distinct csa_space_inbound_id, program_id,modular_catg_nbr from dbo.program_mod_dept_catg where modular_dept_nbr =%d", deptNbr), rs -> {
            List<Mod> mods = new ArrayList<>();
            while (rs.next()) {
                int csaId = rs.getInt("csa_space_inbound_id");
                int modId = rs.getInt("modular_catg_nbr");
                int programId = rs.getInt("program_id");
                String uniqueKey = csaId +"-" + modId+"-" + programId;
                if(!checkIfPresentMod(uniqueKey,mods)){
                    mods.add(new Mod(null,uniqueKey,modId,null,programId,null,null,null,deptNbr,null));
                }
            }
            return mods;
        });
    }

    private boolean checkIfPresentMod(String uniqueKey, List<Mod> mods) {
        return mods.stream().anyMatch(mod->uniqueKey.equals(mod.getUniqueKey()));
    }

    @Override
    public boolean updateCSASeasonStartAndEnd(CSASeasonInfo csaSeasonInfo, GenerateProgramsDTO generateProgramsDTO) {
        if (!csaSeasonInfo.getSeasonStartWk().equals(generateProgramsDTO.getSeasonStartWk()) ) {
            strategyJdbcTemplate.update(String.format("Update dbo.program_csa_space_inbound set csa_start_yr_wk_ccyyww = %d  where csa_space_inbound_id = %d", generateProgramsDTO.getSeasonStartWk(), csaSeasonInfo.getCsaId()));
        }
        if ( !csaSeasonInfo.getSeasonEndWk().equals(generateProgramsDTO.getSeasonEndWk())) {
            strategyJdbcTemplate.update(String.format("Update dbo.program_csa_space_inbound set csa_end_yr_wk_ccyyww= %d where csa_space_inbound_id = %d", generateProgramsDTO.getSeasonEndWk(),  csaSeasonInfo.getCsaId()));
        }
        strategyJdbcTemplate.update("Update dbo.program_csa_space_inbound set csa_space_file_name= ? where csa_space_inbound_id = ?" ,generateProgramsDTO.getFileName(),  csaSeasonInfo.getCsaId());
        strategyJdbcTemplate.update("Update dbo.program_csa_space_inbound set last_modified_ts= ? where csa_space_inbound_id = ?" ,Timestamp.valueOf(LocalDateTime.now()),  csaSeasonInfo.getCsaId());

        return true;
    }

    @Override
    public boolean saveAll(Collection<ProgramDTO> programs, int csaId,  int deptNbr) {
        persistNewPrograms(programs, csaId, deptNbr);
        upsertMods(programs, csaId);
        return true;
    }

    private void upsertMods(Collection<ProgramDTO> programs, int csaId) {
        strategyJdbcTemplate.update("delete from dbo.program_mod_dept_catg_fixture  where csa_space_inbound_id = ?", csaId);
        strategyJdbcTemplate.update("delete from dbo.program_mod_dept_catg  where csa_space_inbound_id =?", csaId);

        List<Mod> mods = new ArrayList<>();
        programs.forEach(programDTO -> mods.addAll(programDTO.getMods()));
        strategyJdbcTemplate.batchUpdate("INSERT INTO dbo.program_mod_dept_catg (program_id, modular_dept_nbr, modular_catg_nbr,csa_space_inbound_id,modular_catg_desc,merchant_email,first_name,last_name,userid,merchant_domain,create_userid,create_ts,last_modified_userid,last_modified_ts) " +
                        "VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?)",
                mods,
                100,
                (PreparedStatement ps, Mod mod) -> {
                    ps.setLong(1, mod.getProgramId());
                    ps.setInt(2, mod.getDeptNbr());
                    ps.setInt(3, mod.getModId());
                    ps.setInt(4, csaId);
                    ps.setString(5, mod.getModName());
                    ps.setString(6, null);
                    ps.setString(7, mod.getFirstName());
                    ps.setString(8, mod.getLastName());
                    ps.setString(9, null);
                    ps.setString(10, null);
                    ps.setString(11, Constant.AEX_STRATEGY_SERVICE);
                    ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(13, Constant.AEX_STRATEGY_SERVICE);
                    ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
                });

        insertFixtures(csaId, mods);
    }

    private void insertFixtures(int csaId, List<Mod> mods) {
        List<ModFixture> modFixtures = new ArrayList<>();
        mods.forEach(mod -> mod.getFixtureType().forEach(fixture ->{
            modFixtures.add(new ModFixture(mod.getModName(), mod.getUniqueKey(), mod.getModId(),mod.getStoreList(),mod.getProgramId(),mod.getProgramName(),mod.getFirstName(),mod.getLastName(),mod.getDeptNbr(),fixture));
        }));

        strategyJdbcTemplate.batchUpdate("INSERT INTO dbo.program_mod_dept_catg_fixture (program_id,csa_space_inbound_id, modular_dept_nbr, modular_catg_nbr,fixturetype_rollup_id,create_userid,create_ts,last_modified_userid,last_modified_ts) " +
                        "VALUES (?, ?, ?,?, ?, ?,?, ?, ?)",
                modFixtures,
                100,
                (PreparedStatement ps, ModFixture mod) -> {
                    ps.setLong(1, mod.getProgramId());
                    ps.setInt(2, csaId);
                    ps.setInt(3, mod.getDeptNbr());
                    ps.setInt(4, mod.getModId());
                    ps.setInt(5, FixtureTypeRollup.getFixtureIdFromNamePluralSingular(mod.getFixtureType()));
                    ps.setString(6, Constant.AEX_STRATEGY_SERVICE);
                    ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(8, Constant.AEX_STRATEGY_SERVICE);
                    ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                });
    }

    private void persistNewPrograms(Collection<ProgramDTO> programs, int csaId, int deptNbr) {
        List<ProgramDTO> programDTOSToSave = filterProgramsToSave(programs);
        strategyJdbcTemplate.batchUpdate("INSERT INTO dbo.program (program_id, aex_program_name, trait_nbr,merch_dept_nbr,trait_last_maint_ts,trait_last_maint_userid,trait_short_desc,trait_long_desc,pgm_create_userid,pgm_last_change_userid,pgm_create_ts,pgm_last_change_ts,program_err_msg,acct_dept_nbr,csa_space_inbound_id) " +
                        "VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?)", programDTOSToSave
                ,
                100,
                (PreparedStatement ps, ProgramDTO programDTO) -> {
                    String programName = programDTO.getProgramName();
                        ps.setLong(1, programDTO.getProgramId());
                        ps.setString(2, programName);
                        ps.setInt(3, -1);
                        ps.setInt(4, deptNbr);
                        ps.setTimestamp(5, null);
                        ps.setTimestamp(6, null);
                        ps.setString(7, programName.substring(0, Math.min(programName.length(), 59)));
                        ps.setString(8, programName);
                        ps.setString(9, Constant.AEX_STRATEGY_SERVICE);
                        ps.setString(10, Constant.AEX_STRATEGY_SERVICE);
                        ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setString(13, null);
                        ps.setInt(14, deptNbr);
                        ps.setInt(15, csaId);
                });
    }

    @NotNull
    private List<ProgramDTO> filterProgramsToSave(Collection<ProgramDTO> programs) {
        return programs.stream().filter(programDTO -> programDTO.getCsaSpaceInboundId() == null)
                .peek(this::setProgramIds)
                .collect(Collectors.toList());
    }

    private void setProgramIds(ProgramDTO programDTO) {
        if (programDTO.getProgramId() == null) {
            int programId = getMaxId("SELECT NEXT VALUE FOR dbo.seq_ap_count AS maxCount");
            programDTO.setProgramId(programId);
            programDTO.getMods().forEach(mod -> mod.setProgramId(programId));
        }
    }

    @Override
    public CSASeasonInfo fetchOrPersistCSAId(GenerateProgramsDTO dto) {
        CSASeasonInfo csaSeasonInfo = findExistingCSAInboundId(dto);
        if(csaSeasonInfo == null){
            csaSeasonInfo = insertCSAInboundId(dto);
        }
        return csaSeasonInfo;
    }

    @NotNull
    private CSASeasonInfo insertCSAInboundId(GenerateProgramsDTO dto) {
        CSASeasonInfo csaSeasonInfo;
        Integer maxId = getMaxId("select max(csa_space_inbound_id) as maxCount from dbo.program_csa_space_inbound");
        int csaId = maxId != null ? maxId + 1 : 1;
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withSchemaName("dbo").withTableName("program_csa_space_inbound");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csa_space_inbound_id", csaId);
        parameters.put("csa_season_code", dto.getSeasonCode().substring(0,2));
        parameters.put("fiscal_year", dto.getFiscalYear());
        parameters.put("csa_space_season_code",  dto.getCsaSeasonId());
        parameters.put("modular_dept_nbr", dto.getDeptNbr());
        parameters.put("csa_start_yr_wk_ccyyww", dto.getSeasonStartWk());
        parameters.put("csa_end_yr_wk_ccyyww", dto.getSeasonEndWk());
        parameters.put("csa_space_file_name", dto.getFileName());
        parameters.put("create_userid",  Constant.AEX_STRATEGY_SERVICE);
        parameters.put("create_ts",  Timestamp.valueOf(LocalDateTime.now()));
        parameters.put("last_modified_userid",  Constant.AEX_STRATEGY_SERVICE);
        parameters.put("last_modified_ts",  Timestamp.valueOf(LocalDateTime.now()));
        simpleJdbcInsert.execute(parameters);
        csaSeasonInfo = new CSASeasonInfo(csaId, dto.getSeasonStartWk(), dto.getSeasonEndWk(),false);
        return csaSeasonInfo;
    }

    @NotNull
    private Integer getMaxId(String sql) {
        return Objects.requireNonNull(strategyJdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return rs.getInt("maxCount");
            }
            return null;
        }));
    }

    @Nullable
    private CSASeasonInfo findExistingCSAInboundId(GenerateProgramsDTO dto) {
        return strategyJdbcTemplate.query(String.format("select csa_space_inbound_id, csa_start_yr_wk_ccyyww , csa_end_yr_wk_ccyyww from dbo.program_csa_space_inbound where csa_space_season_code =%d", dto.getCsaSeasonId()), rs -> {
            if (rs.next()) {
                return new CSASeasonInfo(rs.getInt("csa_space_inbound_id"), rs.getInt("csa_start_yr_wk_ccyyww"), rs.getInt("csa_end_yr_wk_ccyyww"), true);
            }
            return null;
        });
    }

    @Override

    public HashMap<String, Integer> findAllProgramIdsAndNames(int deptNbr) {
        return strategyJdbcTemplate.query(String.format("select distinct program_id,aex_program_name from dbo.program where merch_dept_nbr =%d", deptNbr), rs -> {
            HashMap<String, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("aex_program_name").toLowerCase(), rs.getInt("program_id"));
            }
            return result;
        });
    }

    @Override

    public HashSet<ProgramDTO> findAllCSAIdsProgramIdsAndNames(int deptNbr) {
        return strategyJdbcTemplate.query(String.format("select distinct csa_space_inbound_id, program_id,aex_program_name from dbo.program where merch_dept_nbr =%d", deptNbr), rs -> {
            HashSet<ProgramDTO> allPrograms = new HashSet<>();
            while (rs.next()) {
                int csaId = rs.getInt("csa_space_inbound_id");
                String programName = rs.getString("aex_program_name").toLowerCase();
                int programId = rs.getInt("program_id");
                if(!checkIfPresent(csaId,programName,allPrograms)){
                    allPrograms.add(new ProgramDTO(programId,csaId,programName,null,null));
                }
            }
            return allPrograms;
        });
    }

    private boolean checkIfPresent(int csaId, String programName, HashSet<ProgramDTO> allPrograms) {
        return allPrograms.stream().anyMatch(programDTO ->  programDTO.getProgramName().equals(programName) && programDTO.getCsaSpaceInboundId().equals(csaId));
    }

    @Override
    public boolean delete(Map<String, ProgramDTO> modsFromExcel, Integer csaId, HashSet<ProgramDTO> programDTOS, List<Integer> planIds) {
        if (programDTOS.size() > 0) {
            List<ProgramDTO> deletes = programDTOS.stream().filter(programDTO -> programDTO.getCsaSpaceInboundId().equals(csaId) && !modsFromExcel.containsKey(programDTO.getProgramName().toLowerCase())).collect(Collectors.toList()); //finds the programs to delete
            deletes.forEach(programDTO -> {
                if (!CollectionUtils.isEmpty(planIds)) {
                    strategyJdbcTemplate.update("UPDATE a SET select_status_id = 0 FROM dbo.elig_fl_clus_prog a WHERE a.plan_id IN (?) AND a.program_id = ?", planIds, programDTO.getProgramId());
                    strategyJdbcTemplate.update("UPDATE a SET select_status_id = 0 FROM dbo.elig_style_clus_prog a WHERE a.plan_id IN (?) AND a.program_id = ?", planIds, programDTO.getProgramId());
                    strategyJdbcTemplate.update("UPDATE a SET select_status_id = 0 FROM dbo.elig_cc_clus_prog a WHERE a.plan_id IN (?) AND a.program_id = ?", planIds, programDTO.getProgramId());
                    strategyJdbcTemplate.update("UPDATE a SET select_status_id = 0 FROM dbo.elig_fl_mkt_clus_prog a WHERE a.plan_id IN (?) AND a.program_id = ?", planIds, programDTO.getProgramId());
                    strategyJdbcTemplate.update("UPDATE a SET select_status_id = 0 FROM dbo.elig_cc_mkt_clus_prog a WHERE a.plan_id IN (?) AND a.program_id = ?", planIds, programDTO.getProgramId());
                }

                strategyJdbcTemplate.update("delete from dbo.program_mod_dept_catg_fixture  where csa_space_inbound_id = ? and program_id=?", csaId, programDTO.getProgramId());
                strategyJdbcTemplate.update("delete from dbo.program_mod_dept_catg  where csa_space_inbound_id =? and program_id=?", csaId, programDTO.getProgramId());
                strategyJdbcTemplate.update("delete from dbo.program  where csa_space_inbound_id = ? and program_id=?", csaId, programDTO.getProgramId());
            });
        }
        return false;
    }


}
