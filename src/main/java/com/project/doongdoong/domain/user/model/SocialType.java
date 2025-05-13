package com.project.doongdoong.domain.user.model;

import com.project.doongdoong.domain.user.exeception.SocialTypeNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Getter
@Slf4j
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    private final String description;

    public static SocialType findSocialTypeBy(String socialType) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(socialType))
                .findFirst()
                .orElseThrow(SocialTypeNotFoundException::new);
    }

}
