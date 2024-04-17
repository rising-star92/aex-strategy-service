package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyFinelineId;
import com.walmart.aex.strategy.entity.StratergyFLVG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StratergyFLVGRepository extends JpaRepository<StratergyFLVG,StrategyFinelineId>{

}
