package com.project.doongdoong.domain.counsel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CounselType {

    LOVE("연애"),
    JOB("취업진로"),
    MENTAL_HEALTH("정신건강"),
    RELATIONSHIP("대인관계"),
    FAMILY("가족"),
    ETC("기타");

    private final String description;

    public static CounselType generateCounselTypeFrom(String counselTypeName) {

        return Arrays.stream(CounselType.values())
                .filter(counselType -> counselType.getDescription().equals(counselTypeName))
                .findAny()
                .orElse(ETC);
    }

}
