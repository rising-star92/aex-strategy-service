package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTraitProgram {

	private Integer deptId;
	private Integer catgId;
	private Integer startYrWeek;
	private Integer endYrWeek;
}
