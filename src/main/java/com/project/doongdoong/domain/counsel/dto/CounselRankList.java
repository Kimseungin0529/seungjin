package com.project.doongdoong.domain.counsel.dto;

import com.project.doongdoong.domain.counsel.model.CounselRank;

import java.util.List;

public record CounselRankList(
        List<CounselRank> totalRanking,
        List<CounselRank> weeksRanking
) {
    public static CounselRankList of(List<CounselRank> totalRanking, List<CounselRank> weeksRanking) {
        return new CounselRankList(totalRanking, weeksRanking);
    }
}
