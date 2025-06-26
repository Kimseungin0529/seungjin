package com.project.doongdoong.domain.counsel.dto.response;

import com.project.doongdoong.domain.counsel.dto.CounselRankList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CounselListResponse {

    private int currentPage;
    private int numberPerPage;
    private int totalPage;
    private long totalElements;
    private List<CounselResponse> counselContent;
    private CounselRankList counselRankList;

    @Builder
    public CounselListResponse(int currentPage, int numberPerPage, int totalPage, long totalElements, List<CounselResponse> counselContent) {
        this.currentPage = currentPage;
        this.numberPerPage = numberPerPage;
        this.totalPage = totalPage;
        this.totalElements = totalElements;
        this.counselContent = counselContent;
    }

    public static CounselListResponse of(CounselListResponse response, CounselRankList combinedRanking) {
        return new CounselListResponse(
                response.currentPage,
                response.getNumberPerPage(),
                response.totalPage,
                response.totalElements,
                response.getCounselContent(),
                combinedRanking
        );
    }
}
