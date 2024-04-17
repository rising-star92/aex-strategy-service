package com.walmart.aex.strategy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.Finelines;
import com.walmart.aex.strategy.dto.Rank.RankRequest;
import com.walmart.aex.strategy.dto.Rank.RankResponse;
import com.walmart.aex.strategy.dto.VDRequest;
import com.walmart.aex.strategy.dto.VDResponseDTO;
import com.walmart.aex.strategy.dto.request.FPVolumeDeviationUserSelectionRequest;
import com.walmart.aex.strategy.entity.StratergyFLVG;
import com.walmart.aex.strategy.exception.VDException;
import com.walmart.aex.strategy.repository.StratergyFLVGRepository;
import com.walmart.aex.strategy.service.FlowPlanService;
import com.walmart.aex.strategy.service.RankService;
import com.walmart.aex.strategy.service.VolumeDeviationService;
import graphql.GraphQLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequestMapping("/flowPlan")
public class FlowPlanController {
    private final String SUCCESS_MESSAGE = "Successfully Saved";

    @Autowired
    private FlowPlanService flowPlanService;

    private final RankService rankService;

    private StratergyFLVGRepository stratergyFLVGRepository;

    @Autowired
    private VolumeDeviationService volumeDeviationService;

    public FlowPlanController(RankService rankService) {
        this.rankService = rankService;
    }

    @PostMapping(path = "/saveVolumeDeviationUserSelection")
    public @ResponseBody
    ResponseEntity setVoulmeDeviation(@RequestBody FPVolumeDeviationUserSelectionRequest request) throws JsonProcessingException {
        if (!ObjectUtils.isEmpty(request)) {
            String responseMessage = flowPlanService.saveVolumeDeviationUserSelection(request);
            if (!StringUtils.isBlank(responseMessage) && SUCCESS_MESSAGE.equalsIgnoreCase(responseMessage)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Save");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Save .Input Data Issue");

    }


    @QueryMapping(name = "getVolumeDeviationStrategySelection")
    public VDResponseDTO getVolumeDeviationMetrics(@Argument VDRequest request, GraphQLContext context) throws VDException, FileNotFoundException {
        return volumeDeviationService.getVolumeDeviationCategory(request, context.getOrDefault(HttpHeaders.AUTHORIZATION, ""));
    }

    @QueryMapping
    public RankResponse getFinelineRank(@Argument RankRequest request) {
        return rankService.fetchFinelineRank(request);
    }

    @QueryMapping
    public RankResponse getCcRank(@Argument RankRequest request) {
        return rankService.fetchCcRank(request);
    }

    @PostMapping(path = "/saveStratFlVgData")
    public @ResponseBody
    ResponseEntity saveStratFlVgData(@RequestBody List<Finelines> finelinesList) throws JsonProcessingException {
        if (!ObjectUtils.isEmpty(finelinesList)) {
            String responseMessage = flowPlanService.saveDefaultVolumeDeviation(finelinesList);
            if (!StringUtils.isBlank(responseMessage) && SUCCESS_MESSAGE.equalsIgnoreCase(responseMessage)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Save");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Save .Input Data Issue");
    }
}
