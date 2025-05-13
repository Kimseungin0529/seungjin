package com.project.doongdoong.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SocialIdentifierTest {

    @DisplayName("사용자에 대한 소셜식별자 고유키를 반환합니다.")
    @Test
    void toUniqueValue() {
        // given
        String socialId = "12345678";
        String socialType = SocialType.APPLE.getDescription();
        SocialIdentifier socialIdentifier = SocialIdentifier.of(socialId, socialType);
        // when
        String result = socialIdentifier.toUniqueValue();
        // then
        assertThat(result)
                .isEqualTo(socialId + "_" + socialType);
    }

    @DisplayName("고유키를 통해 소셜식별자로 변환합니다.")
    @Test
    void from() {
        // given
        String socialId = "12345678";
        String socialType = "APPLE";
        String uniqueKey = socialId + "_" + socialType;
        // when
        SocialIdentifier result = SocialIdentifier.from(uniqueKey);
        // then
        assertThat(result)
                .extracting(SocialIdentifier::getSocialType, SocialIdentifier::getSocialId)
                .contains(SocialType.APPLE, socialId);
    }



}