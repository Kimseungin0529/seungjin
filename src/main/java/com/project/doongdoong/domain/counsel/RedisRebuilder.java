package com.project.doongdoong.domain.counsel;

import com.project.doongdoong.domain.counsel.model.CounselCacheKey;
import com.project.doongdoong.domain.counsel.repository.CounselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRebuilder {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CounselRepository counselRepository; // 또는 JPA Query로 구현

    public void rebuildRedisFromRdb() {
        List<Object[]> results = counselRepository.countCounselGroupByDateAndType();

        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            String type = row[1].toString();
            Long count = (Long) row[2];

            // 날짜별 key
            String dailyKey = CounselCacheKey.generateDailyKey(date);
            redisTemplate.opsForZSet().incrementScore(dailyKey, type, count);

            // TTL 설정: 오늘 기준 7일 이내만 TTL 설정
            if (!date.isBefore(LocalDate.now().minusDays(7))) {
                redisTemplate.expire(dailyKey, 7, TimeUnit.DAYS);
            }

            // 전체 key
            String totalKey = CounselCacheKey.generateTotalKey();
            redisTemplate.opsForZSet().incrementScore(totalKey, type, count);
        }
    }
}
