package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.Weeks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FiscalWeekRepository extends JpaRepository<Weeks, Integer> {
    @Query(value = "select max(fw.wmWeek) from Weeks fw Join FiscalYear fy On fw.fiscalYearId = fw.fiscalYearId Where fy.fiscalYearValue = :fiscalYear")
    Integer getMaxWeeksForWmYear(Integer fiscalYear);
}
