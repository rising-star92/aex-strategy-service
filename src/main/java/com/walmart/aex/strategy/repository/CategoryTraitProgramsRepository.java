package com.walmart.aex.strategy.repository;

import java.util.List;

import com.walmart.aex.strategy.dto.CategoryTraitProgram;

public interface CategoryTraitProgramsRepository {
	List<CategoryTraitProgram> getCategoryTraitPrograms(Long planId);
}
