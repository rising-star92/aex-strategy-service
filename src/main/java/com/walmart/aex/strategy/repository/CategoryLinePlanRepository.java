package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.entity.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryLinePlanRepository extends CrudRepository<CategoryLineplan, CategoryLineplanId> {

    @Query("SELECT new com.walmart.aex.strategy.entity.TargetCounts(sum(clp.flCount), sum(clp.ccCount)) from CategoryLineplan clp where clp.categoryLineplanId.planId = :planId and clp.categoryLineplanId.channelId <> :channelId")
    List<TargetCounts> findTargetCounts(long planId,int channelId);

    @Query("SELECT clp.categoryLineplanId.lvl3Nbr as lvl3Nbr , count(sclp.subCategoryLineplanId.lvl4Nbr) as count from CategoryLineplan clp " +
            "join SubCategoryLineplan sclp on clp.categoryLineplanId.planId = sclp.subCategoryLineplanId.planId and clp.categoryLineplanId.lvl3Nbr = sclp.subCategoryLineplanId.lvl3Nbr and clp.categoryLineplanId.channelId = sclp.subCategoryLineplanId.channelId " +
            "where clp.categoryLineplanId.planId = :planId and clp.categoryLineplanId.channelId <> :channelId " +
            "group by clp.categoryLineplanId.lvl3Nbr")
    List<Object[]> fetchLvl3ToLvl4Map(long planId, int channelId);

    @Modifying
    @Query("update CategoryLineplan c set c.flCount = :flCount where c.categoryLineplanId.planId = :planId and c.categoryLineplanId.channelId= :channelId and c.categoryLineplanId.lvl3Nbr= :lvl3Nbr")
    void updateFlCount(int flCount, long planId, int channelId, int lvl3Nbr  );

    @Modifying
    @Query("update CategoryLineplan c set c.ccCount = :ccCount where c.categoryLineplanId.planId = :planId and c.categoryLineplanId.channelId= :channelId and c.categoryLineplanId.lvl3Nbr= :lvl3Nbr")
    void updateCcCount(int ccCount, long planId, int channelId, int lvl3Nbr  );

    @Query("select new com.walmart.aex.strategy.dto.LinePlanCount(categoryLineplanId.lvl0Nbr AS lvl0Nbr, categoryLineplanId.lvl1Nbr AS lvl1Nbr, categoryLineplanId.lvl2Nbr AS lvl2Nbr, categoryLineplanId.lvl3Nbr as lvl3Nbr, flCount, ccCount, attributeObj) from CategoryLineplan where categoryLineplanId.planId = :planId and categoryLineplanId.strategyId = :strategyId and categoryLineplanId.channelId = :channelId")
    List<LinePlanCount> getCategoryAttributeStrategy(long planId, int channelId, long strategyId);

    @Query("select new com.walmart.aex.strategy.dto.LinePlanCount(categoryLineplanId.lvl0Nbr AS lvl0Nbr, categoryLineplanId.lvl1Nbr AS lvl1Nbr, categoryLineplanId.lvl2Nbr AS lvl2Nbr, categoryLineplanId.lvl3Nbr as lvl3Nbr, flCount, ccCount, attributeObj) from CategoryLineplan where categoryLineplanId.planId = :planId and categoryLineplanId.channelId = :channelId and categoryLineplanId.strategyId = :strategyId and categoryLineplanId.lvl3Nbr = :lvl3Nbr and categoryLineplanId.lvl2Nbr = :lvl2Nbr and categoryLineplanId.lvl1Nbr = :lvl1Nbr and categoryLineplanId.lvl0Nbr = :lvl0Nbr")
    LinePlanCount getCategoryAttributeGrpStrategy(long planId, int channelId, long strategyId, int lvl3Nbr, int lvl2Nbr, int lvl1Nbr, int lvl0Nbr);

    @Modifying
    @Query("update CategoryLineplan c set c.attributeObj = :attributeObj where c.categoryLineplanId.planId = :planId and c.categoryLineplanId.channelId= :channelId and c.categoryLineplanId.lvl3Nbr= :lvl3Nbr and c.categoryLineplanId.lvl2Nbr= :lvl2Nbr and c.categoryLineplanId.strategyId = :strategyId and c.categoryLineplanId.lvl1Nbr = :lvl1Nbr and c.categoryLineplanId.lvl0Nbr = :lvl0Nbr")
    void updateAttributeGroup(long planId, int channelId, int lvl3Nbr,int lvl2Nbr,long strategyId,int lvl1Nbr, int lvl0Nbr, String attributeObj);

    @Query("SELECT distinct c.categoryLineplanId.lvl3Nbr as lvl3Nbr from CategoryLineplan c where c.categoryLineplanId.planId = :planId and c.categoryLineplanId.lvl2Nbr= :lvl2Nbr " +
            "and c.categoryLineplanId.strategyId = :strategyId and c.categoryLineplanId.lvl1Nbr = :lvl1Nbr and c.categoryLineplanId.lvl0Nbr = :lvl0Nbr ")
    List<Integer> getCategories(long planId, long strategyId,int lvl2Nbr, int lvl1Nbr, int lvl0Nbr);

}
