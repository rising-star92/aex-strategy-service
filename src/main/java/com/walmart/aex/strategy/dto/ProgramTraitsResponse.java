package com.walmart.aex.strategy.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProgramTraitsResponse {
	private List<Trait> trait;
	private String status;
	private String message;

	public ProgramTraitsResponse(List<Trait> trait){
		this.trait=trait;
	}
}
