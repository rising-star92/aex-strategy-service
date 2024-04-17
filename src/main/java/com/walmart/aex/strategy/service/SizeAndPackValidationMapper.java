package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.ValidationSPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SizeAndPackValidationMapper {

    public ValidationSPResponse getFinelineValidation(SizeResponseDTO sizeResponseDTO, List<SizeResponseDTO> validationSizeResponseList) {
        if (Objects.isNull(validationSizeResponseList) || validationSizeResponseList.isEmpty()) {
            return null;
        }
        List<SizeResponseDTO> filteredValidationResponseList = validationSizeResponseList.stream()
                .filter(size -> size.getFinelineNbr().equals(sizeResponseDTO.getFinelineNbr()))
                .collect(Collectors.toList());
        List<Long> allCcTotalSizePct = getAllCcTotalSizePct(filteredValidationResponseList, true);
        return ValidationSPResponse.builder()
                .merchMethodCodeList(filteredValidationResponseList.stream()
                        .map(SizeResponseDTO::getMerchMethodCode).collect(Collectors.toSet()))
                .sizeProfilePctList(allCcTotalSizePct.stream().limit(3).collect(Collectors.toSet()))
                .build();
    }

    public Set<Long> getStyleValidation(SizeResponseDTO sizeResponseDTO, List<SizeResponseDTO> validationSizeResponseList) {
        if (Objects.isNull(validationSizeResponseList) || validationSizeResponseList.isEmpty()) {
            return Collections.emptySet();
        }
        List<SizeResponseDTO> filteredValidationResponseList = validationSizeResponseList.stream()
                .filter(size -> size.getStyleNbr(). equalsIgnoreCase(sizeResponseDTO.getStyleNbr()) && size.getCcId().equalsIgnoreCase(sizeResponseDTO.getCcId()))
                .collect(Collectors.toList());

        List<Long> allCcTotalSizePct = getAllCcTotalSizePct(filteredValidationResponseList, false);

        return allCcTotalSizePct.stream().limit(3).collect(Collectors.toSet());
    }

    public ValidationSPResponse getCcValidation(SizeResponseDTO sizeResponseDTO, List<SizeResponseDTO> validationSizeResponseList, boolean isSizeAssc) {
        if (Objects.isNull(validationSizeResponseList) || validationSizeResponseList.isEmpty()) {
            return null;
        }

        List<SizeResponseDTO> filteredValidationResponseList = validationSizeResponseList.stream()
                .filter(size -> size.getCcId().equalsIgnoreCase(sizeResponseDTO.getCcId()))
                .collect(Collectors.toList());

        List<Long> allTotalSizePct = getAllCcTotalSizePct(filteredValidationResponseList, isSizeAssc);
        return ValidationSPResponse.builder()
                .sizeProfilePctList(allTotalSizePct.stream().limit(3).collect(Collectors.toSet()))
                .build();
    }

    private static List<Long> getAllCcTotalSizePct(List<SizeResponseDTO> filteredValidationResponseList, boolean isFinelineLevel) {
        return filteredValidationResponseList.stream()
                .map(SizeResponseDTO::getTotalSizeProfilePct)
                .filter(sizePct -> Objects.nonNull(sizePct) && (isFinelineLevel || !sizePct.equals(-1.00)))
                .map(Math::round)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}
