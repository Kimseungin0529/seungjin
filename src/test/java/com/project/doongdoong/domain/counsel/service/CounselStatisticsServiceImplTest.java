package com.project.doongdoong.domain.counsel.service;

import com.project.doongdoong.domain.counsel.dto.CounselRankList;
import com.project.doongdoong.domain.counsel.model.CounselCacheKey;
import com.project.doongdoong.domain.counsel.model.CounselRank;
import com.project.doongdoong.domain.counsel.model.CounselType;
import com.project.doongdoong.module.IntegrationSupportTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CounselStatisticsServiceImplTest extends IntegrationSupportTest {

    @Autowired
    CounselStatisticsService counselStatisticsService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }


    @DisplayName("상담 주제 별 개수 통계 시나리오")
    @TestFactory
    Collection<DynamicTest> incrementTypeCount() {
        // given
        String totalKey = CounselCacheKey.generateTotalKey();
        LocalDate now = LocalDate.now();
        String dailyKey = CounselCacheKey.generateDailyKey(now);

        CounselType counselTypeLove = CounselType.LOVE;
        redisTemplate.opsForZSet().incrementScore(totalKey, counselTypeLove.name(), 21);
        redisTemplate.opsForZSet().add(dailyKey, counselTypeLove.name(), 3);
        int days = 0;
        for (int i = 0; i < 7; i++) {
            days += i;
            redisTemplate.opsForZSet().add(CounselCacheKey.generateDailyKey(now.minusDays(i)), counselTypeLove.name(), i);
        }
        int finalDays = days;

        // when & then
        return List.of(
                DynamicTest.dynamicTest("상담 유형에 해당하는 값을 증가시킵니다.", () -> {
                    //given
                    CounselType counselType = CounselType.JOB;
                    // when
                    counselStatisticsService.incrementTypeCount(counselType);
                    // then

                    assertThat(redisTemplate.opsForZSet().score(totalKey, counselTypeLove.name()))
                            .isEqualTo(finalDays);
                    assertThat(redisTemplate.opsForZSet().score(totalKey, counselType.name()))
                            .isEqualTo(1.0);
                    assertThat(redisTemplate.opsForZSet().score(dailyKey, counselType.name()))
                            .isEqualTo(1.0);
                }),
                DynamicTest.dynamicTest("이어서 상담 유형에 해당하는 값을 증가시킵니다", () -> {
                    // when
                    counselStatisticsService.incrementTypeCount(counselTypeLove);
                    // then
                    assertThat(redisTemplate.opsForZSet().score(totalKey, counselTypeLove.name()))
                            .isEqualTo(finalDays + 1);
                    assertThat(redisTemplate.opsForZSet().score(dailyKey, counselTypeLove.name()))
                            .isEqualTo(1.0);
                })
        );


    }

    @Test
    @DisplayName("종합한 순위 정보를 반환한다.")
    void getCombinedRanking() {
        // given
        String totalKey = CounselCacheKey.generateTotalKey();
        String weekKey = CounselCacheKey.WEEKS_COUNT.getPattern();

        redisTemplate.opsForZSet().add(totalKey, "LOVE", 42);
        redisTemplate.opsForZSet().add(weekKey, "LOVE", 20);
        redisTemplate.opsForZSet().add(weekKey, "JOB", 24);

        // when
        CounselRankList result = counselStatisticsService.getCombinedRanking();

        // then
        Assertions.assertAll(
                () -> assertThat(result.totalRanking())
                        .extracting(CounselRank::count, CounselRank::counselType)
                        .containsExactly(
                                tuple(42.0, "LOVE")
                        ),
                () -> assertThat(result.weeksRanking())
                        .extracting(CounselRank::count, CounselRank::counselType)
                        .containsExactly(
                                tuple(24.0, "JOB"),
                                tuple(20.0, "LOVE")
                        )
        );

    }

    @Test
    @DisplayName("종합된 순위 정보가 없으면 빈 값을 반환한다")
    void getCombinedRanking_whenEmpty_shouldReturnEmptyList() {
        // given
        redisTemplate.delete(CounselCacheKey.generateTotalKey());
        redisTemplate.delete(CounselCacheKey.WEEKS_COUNT.getPattern());

        // when
        CounselRankList result = counselStatisticsService.getCombinedRanking();

        // then
        assertThat(result.totalRanking()).isEmpty();
        assertThat(result.weeksRanking()).isEmpty();
    }


}