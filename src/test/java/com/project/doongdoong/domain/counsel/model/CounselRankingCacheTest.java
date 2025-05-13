package com.project.doongdoong.domain.counsel.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CounselRankingCacheTest {

    @DisplayName("날짜와 상담 유형에 대한 상담 키를 발급합니다.")
    @Test
    void generateDailyKey() {
        // given
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        // when
        String result = CounselCacheKey.generateDailyKey(localDate);
        // then
        assertThat(result)
                .isEqualTo("counsel:" + localDate + ":count");
    }

    @DisplayName("상담 유형에 대한 상담 키를 발급합니다.")
    @Test
    void generateTotalKey() {
        // when
        String result = CounselCacheKey.generateTotalKey();
        // then
        assertThat(result)
                .isEqualTo("counsel:total:count");
    }


}