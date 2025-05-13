package com.project.doongdoong.domain.counsel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CounselCacheKey {
    TOTAL_COUNT("counsel:total:count"),
    DAY_COUNT("counsel:%s:count"),
    WEEKS_COUNT("counsel:weeks:count"),
    ;

    private final String pattern;

    public static String generateDailyKey(LocalDate localDate) {
        return String.format(DAY_COUNT.pattern, localDate.toString());
    }

    public static String generateTotalKey() {
        return String.format(TOTAL_COUNT.pattern);
    }

    public static List<String> generateKeysBefore(LocalDate localDate, int days){
        List<String> keys = new ArrayList<>();
        for(int i=1; i<=days; i++){
            String key = String.format(DAY_COUNT.pattern, localDate.minusDays(i).toString());
            keys.add(key);
        }
        return keys;
    }
}
