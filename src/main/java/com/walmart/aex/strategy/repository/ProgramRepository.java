package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Integer> {

    @Query("select distinct p from Program  p where p.longDesc in (:programNames)  and p.createDate >= :createDate and p.userId = :userId and p.deptNbr = :deptNbr")
    List<Program> programAlreadyPresent(@Param("programNames") List<String> programNames, @Param("createDate") LocalDate createTime,@Param("userId") String userId ,@Param("deptNbr") Integer deptNbr);

    @Query("select distinct p from Program  p where p.userId = :userId and p.deptNbr = :deptNbr")
    List<Program> getPrograms_By_DeptNbr(@Param("userId") String userId ,@Param("deptNbr") Integer deptNbr);
}
