package com.project.doongdoong.global.util;

import com.project.doongdoong.domain.counsel.model.CounselRank;
import com.project.doongdoong.domain.counsel.model.CounselType;

import java.util.List;

public interface CounselRankingCache {
    void incrementTodayCount(CounselType type);

    void incrementTotalCount(CounselType type);

    List<CounselRank> getTotalRanking();

    List<CounselRank> getWeeksRanking();
}

