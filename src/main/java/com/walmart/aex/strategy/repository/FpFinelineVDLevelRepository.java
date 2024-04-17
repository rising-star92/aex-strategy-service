package com.walmart.aex.strategy.repository;


import com.walmart.aex.strategy.entity.FpFinelineVDCategory;
import com.walmart.aex.strategy.entity.FpFinelineVDCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FpFinelineVDLevelRepository extends JpaRepository<FpFinelineVDCategory, FpFinelineVDCategoryId> {

    @Query("select vd from FpFinelineVDCategory vd where vd.fpFinelineVDCategoryId.planId= :planId AND vd.fpFinelineVDCategoryId.finelineNbr IN(:finelineNbr)")
   List <FpFinelineVDCategory> findByPlan_idAndFineline_nbr(@Param("planId") Long planId, @Param("finelineNbr") List<Integer> finelineNbr);

}
