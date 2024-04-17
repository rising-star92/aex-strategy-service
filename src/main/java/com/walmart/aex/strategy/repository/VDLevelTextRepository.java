package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyGroup;
import com.walmart.aex.strategy.entity.VDLevelText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VDLevelTextRepository extends JpaRepository<VDLevelText, Integer>{
    Optional<VDLevelText> findVdLevelCodeByVdLevelDesc(String vdLevelDesc);
}
