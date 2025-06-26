package com.project.doongdoong.domain.counsel.controller;

import com.project.doongdoong.domain.counsel.dto.CounselRankList;
import com.project.doongdoong.domain.counsel.dto.request.CounselCreateRequest;
import com.project.doongdoong.domain.counsel.dto.response.CounselDetailResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselListResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselResultResponse;
import com.project.doongdoong.domain.counsel.service.CounselService;
import com.project.doongdoong.domain.counsel.service.CounselStatisticsService;
import com.project.doongdoong.global.annotation.CurrentUser;
import com.project.doongdoong.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/api/v1/counsel")
@RequiredArgsConstructor
public class CounselController {

    private final CounselService counselService;
    private final CounselStatisticsService counselStatisticsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<CounselResultResponse> consult(@CurrentUser String uniqueValue
            , @Valid @RequestBody CounselCreateRequest request, HttpServletResponse response) {

        CounselResultResponse result = counselService.consult(uniqueValue, request);

        URI location = UriComponentsBuilder.fromPath("/api/counsel/{id}")
                .buildAndExpand(result.getCounselId())
                .toUri();

        response.setHeader("Location", location.toString());

        return ApiResponse.of(HttpStatus.CREATED, null, result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CounselDetailResponse> findCounselContent(@CurrentUser String uniqueValue, @PathVariable("id") Long counselId) {

        return ApiResponse.of(HttpStatus.OK, null, counselService.findCounselContent(uniqueValue, counselId));
    }

    @GetMapping
    public ApiResponse<CounselListResponse> findCounsels(@CurrentUser String uniqueValue,
                                                         @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
                                                         @Valid @Min(value = 1, message = "페이지 시작은 최소 1입니다.") int pageNumber) {
        CounselListResponse counselListResponse = counselService.findCounsels(uniqueValue, pageNumber);
        CounselRankList combinedRanking = counselStatisticsService.getCombinedRanking();

        return ApiResponse.of(HttpStatus.OK, null, CounselListResponse.of(counselListResponse, combinedRanking));
    }


    @GetMapping("/popular")
    public ApiResponse<CounselRankList> findCounselRankList() {
        return ApiResponse.of(HttpStatus.OK, null, null);
    }


}
