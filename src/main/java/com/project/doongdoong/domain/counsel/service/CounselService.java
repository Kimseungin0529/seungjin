package com.project.doongdoong.domain.counsel.service;


import com.project.doongdoong.domain.counsel.dto.request.CounselCreateRequest;
import com.project.doongdoong.domain.counsel.dto.response.CounselDetailResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselListResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselResultResponse;

public interface CounselService {
    CounselResultResponse consult(String socialId, CounselCreateRequest request);

    CounselDetailResponse findCounselContent(String socialId, Long counselId);

    CounselListResponse findCounsels(String uniqueValue, int pageNumber);
}
