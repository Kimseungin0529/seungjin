package com.project.doongdoong.global.util;

import com.project.doongdoong.domain.counsel.model.CounselCacheKey;
import com.project.doongdoong.domain.counsel.model.CounselRank;
import com.project.doongdoong.domain.counsel.model.CounselType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CounselRankingRedis implements CounselRankingCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int ONE = 1, WEEKS = 7;

    @Override
    public void incrementTodayCount(CounselType type) {
        String key = CounselCacheKey.generateDailyKey(LocalDate.now());
        redisTemplate.opsForZSet().incrementScore(key, type.name(), ONE);
        redisTemplate.expire(key, WEEKS, TimeUnit.DAYS);
    }

    @Override
    public void incrementTotalCount(CounselType type) {
        String key = CounselCacheKey.generateTotalKey();
        redisTemplate.opsForZSet().incrementScore(key, type.name(), ONE);
    }

    @Override
    public List<CounselRank> getTotalRanking() {
        Set<ZSetOperations.TypedTuple<Object>> entries = redisTemplate.opsForZSet()
                .reverseRangeWithScores(CounselCacheKey.generateTotalKey(), 0, -1);
        return mapToCounselRanks(entries);
    }

    @Override
    public List<CounselRank> getWeeksRanking() {
        String key = CounselCacheKey.WEEKS_COUNT.getPattern();

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            List<String> keys = CounselCacheKey.generateKeysBefore(LocalDate.now(), WEEKS);
            redisTemplate.opsForZSet().unionAndStore(keys.get(0), keys.subList(1, keys.size()), key);
            redisTemplate.expire(key, ONE, TimeUnit.DAYS);
        }

        Set<ZSetOperations.TypedTuple<Object>> entries = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        return mapToCounselRanks(entries);
    }

    private List<CounselRank> mapToCounselRanks(Set<ZSetOperations.TypedTuple<Object>> entries) {
        if (entries == null) return List.of();
        return entries.stream()
                .map(e -> CounselRank.of(
                        e.getValue().toString(),
                        e.getScore() != null ? e.getScore() : 0.0
                ))
                .toList();
    }
}
