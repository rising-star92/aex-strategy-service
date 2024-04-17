package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.AllocationRunTypeText;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AllocRunTypeRepository extends Repository<AllocationRunTypeText, Integer> {

  @Query(value = "SELECT alloc_run_type_code, alloc_run_type_desc from [dbo].[alloc_run_type_text]", nativeQuery = true)
  List<AllocationRunTypeText> fetAllAllocationRunType();
}
