package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.entity.SubCategoryLineplan;
import com.walmart.aex.strategy.entity.SubCategoryLineplanId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface SubcategoryLinePlanRepository extends CrudRepository<SubCategoryLineplan, SubCategoryLineplanId> {

    @Modifying
    @Query("update SubCategoryLineplan c set c.flCount = :flCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId and c.subCategoryLineplanId.lvl4Nbr= :lvl4Nbr")
    void updateFlCount(int flCount, long planId, int channelId, int lvl4Nbr  );

    @Modifying
    @Query("update SubCategoryLineplan c set c.ccCount = :ccCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId and c.subCategoryLineplanId.lvl4Nbr= :lvl4Nbr")
    void updateCcCount(int ccCount, long planId, int channelId, int lvl4Nbr  );

    @Query("select count(c.subCategoryLineplanId.lvl4Nbr) from SubCategoryLineplan c where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr and c.subCategoryLineplanId.channelId= :channelId")
    Integer findCountByLvl3(long planId, int lvl3Nbr ,int channelId);

    @Modifying
    @Query("update SubCategoryLineplan c set c.flCount = :flCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr")
    void updateFlCountByLvl3(int flCount, long planId, int channelId, int lvl3Nbr  );

    @Modifying
    @Query("update SubCategoryLineplan c set c.ccCount = :ccCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr")
    void updateCcCountByLvl3(int ccCount, long planId, int channelId, int lvl3Nbr  );

    @Query("select count(c.subCategoryLineplanId.lvl4Nbr) from SubCategoryLineplan c where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId ")
    Integer findCount(long planId,int channelId);

    @Modifying
    @Query("update SubCategoryLineplan c set c.flCount = :flCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId ")
    void updateFlCountAll(int flCount, long planId, int channelId  );

    @Modifying
    @Query("update SubCategoryLineplan c set c.ccCount = :ccCount where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId ")
    void updateCcCountByAll(int ccCount, long planId, int channelId);

    @Query("select sum(c.flCount) from SubCategoryLineplan c where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr and c.subCategoryLineplanId.channelId= :channelId")
    Integer findFlCountByLvl3(long planId, int lvl3Nbr, int channelId);

    @Query("select sum(c.ccCount) from SubCategoryLineplan c where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr and c.subCategoryLineplanId.channelId= :channelId")
    Integer findCcCountByLvl3(long planId, int lvl3Nbr, int channelId);

    @Query("select new com.walmart.aex.strategy.dto.LinePlanCount(subCategoryLineplanId.lvl0Nbr AS lvl0Nbr, subCategoryLineplanId.lvl1Nbr AS lvl1Nbr, subCategoryLineplanId.lvl2Nbr AS lvl2Nbr, subCategoryLineplanId.lvl3Nbr as lvl3Nbr, subCategoryLineplanId.lvl4Nbr as lvl4Nbr, flCount, ccCount, attributeObj) from SubCategoryLineplan where subCategoryLineplanId.planId = :planId and subCategoryLineplanId.strategyId = :strategyId and subCategoryLineplanId.channelId = :channelId")
    List<LinePlanCount> getSubcategoryAttributeStrategy(long planId, int channelId, long strategyId);

    @Query("select new com.walmart.aex.strategy.dto.LinePlanCount(subCategoryLineplanId.lvl0Nbr AS lvl0Nbr, subCategoryLineplanId.lvl1Nbr AS lvl1Nbr, subCategoryLineplanId.lvl2Nbr AS lvl2Nbr, subCategoryLineplanId.lvl3Nbr as lvl3Nbr, subCategoryLineplanId.lvl4Nbr as lvl4Nbr,flCount, ccCount, attributeObj) from SubCategoryLineplan where subCategoryLineplanId.planId = :planId and subCategoryLineplanId.strategyId = :strategyId and subCategoryLineplanId.channelId = :channelId and subCategoryLineplanId.lvl3Nbr = :lvl3Nbr and subCategoryLineplanId.lvl4Nbr = :lvl4Nbr and subCategoryLineplanId.lvl2Nbr = :lvl2Nbr and subCategoryLineplanId.lvl1Nbr = :lvl1Nbr and subCategoryLineplanId.lvl0Nbr = :lvl0Nbr" )
    LinePlanCount getSubcategoryAttributeGrpStrategy(long planId, int channelId, long strategyId, int lvl3Nbr, int lvl4Nbr, int lvl2Nbr, int lvl1Nbr, int lvl0Nbr);

    @Modifying
    @Query("update SubCategoryLineplan c set c.attributeObj = :attributeObj where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.channelId= :channelId and c.subCategoryLineplanId.lvl3Nbr =:lvl3Nbr and c.subCategoryLineplanId.lvl4Nbr =:lvl4Nbr and c.subCategoryLineplanId.strategyId = :strategyId and c.subCategoryLineplanId.lvl2Nbr =:lvl2Nbr and c.subCategoryLineplanId.lvl1Nbr =:lvl1Nbr and c.subCategoryLineplanId.lvl0Nbr =:lvl0Nbr")
    void updateSubCategoryAttributeGroup( long planId, int channelId, int lvl3Nbr,int lvl4Nbr,long strategyId,int lvl2Nbr,int lvl1Nbr, int lvl0Nbr, String attributeObj);

    @Query("select new com.walmart.aex.strategy.dto.LinePlanCount(subCategoryLineplanId.lvl0Nbr AS lvl0Nbr, subCategoryLineplanId.lvl1Nbr AS lvl1Nbr, subCategoryLineplanId.lvl2Nbr AS lvl2Nbr, subCategoryLineplanId.lvl3Nbr as lvl3Nbr, subCategoryLineplanId.lvl4Nbr as lvl4Nbr, flCount, ccCount, attributeObj) from SubCategoryLineplan where subCategoryLineplanId.planId = :planId and subCategoryLineplanId.strategyId = :strategyId and subCategoryLineplanId.channelId = :channelId and subCategoryLineplanId.lvl3Nbr = :lvl3Nbr and subCategoryLineplanId.lvl2Nbr = :lvl2Nbr and subCategoryLineplanId.lvl1Nbr = :lvl1Nbr and subCategoryLineplanId.lvl0Nbr = :lvl0Nbr")
    List<LinePlanCount> getSubcategoryAttributeGrpStrategy(long planId, int channelId, long strategyId, int lvl3Nbr, int lvl2Nbr, int lvl1Nbr, int lvl0Nbr);

    @Query("select distinct c.subCategoryLineplanId.lvl4Nbr AS lvl4Nbr from SubCategoryLineplan c where c.subCategoryLineplanId.planId = :planId and c.subCategoryLineplanId.strategyId = :strategyId " +
            "and c.subCategoryLineplanId.lvl3Nbr= :lvl3Nbr and c.subCategoryLineplanId.lvl2Nbr =:lvl2Nbr and c.subCategoryLineplanId.lvl1Nbr =:lvl1Nbr and c.subCategoryLineplanId.lvl0Nbr =:lvl0Nbr ")
    List<Integer> getSubCategoryNbr(long planId,long strategyId,int lvl3Nbr, int lvl2Nbr, int lvl1Nbr, int lvl0Nbr);


}
