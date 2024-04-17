package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyStyle;
import com.walmart.aex.strategy.entity.StrategyStyleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyStyleRepository extends JpaRepository<StrategyStyle, StrategyStyleId> {
    Optional<StrategyStyle> findByStrategyStyleId(StrategyStyleId strategyStyleId);

    void deleteStrategyStyleByStrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_planStrategyIdAndStrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyStyleId_StrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyStyleId_StrategyFinelineId_finelineNbrAndStrategyStyleId_styleNbr(
            PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr);
}
