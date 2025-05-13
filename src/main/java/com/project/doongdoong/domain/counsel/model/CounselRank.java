package com.project.doongdoong.domain.counsel.model;

public record CounselRank(
        String counselType,
        double count
) {
    public static CounselRank of(String counselType, double count) {
        return new CounselRank(counselType, count);
    }

}
