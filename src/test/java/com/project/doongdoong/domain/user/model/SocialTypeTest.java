package com.project.doongdoong.domain.user.model;

import com.project.doongdoong.domain.user.exeception.SocialTypeNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"KAKAO", "NAVER", "GOOGLE", "APPLE"})
    @DisplayName("SocialType 에 해당하는 요청값을 제공합니다.")
    void findSocialTypeBy(String stringSocialType) {
        // given
        // when
        SocialType result = SocialType.findSocialTypeBy(stringSocialType);
        // then
        assertThat(result).isInstanceOf(SocialType.class);
    }

    @DisplayName("SocialType 제공 시나리오")
    @TestFactory
    Collection<DynamicTest> findSocialTypeBy_시나리오() {
        // when & then
        return List.of(
                DynamicTest.dynamicTest("카카오 타입을 반환합니다.", () -> {
                    // given
                    String stringKakaoType = "KAKAO";
                    // when
                    SocialType result = SocialType.findSocialTypeBy(stringKakaoType);
                    // then
                    assertThat(result).isEqualTo(SocialType.KAKAO);
                }),
                DynamicTest.dynamicTest("네이버 타입을 반환합니다.", () -> {
                    // given
                    String stringNaverType = "NAVER";
                    // when
                    SocialType result = SocialType.findSocialTypeBy(stringNaverType);
                    // then
                    assertThat(result).isEqualTo(SocialType.NAVER);
                }),
                DynamicTest.dynamicTest("구글 타입을 반환합니다.", () -> {
                    // given
                    String stringGoogleType = "GOOGLE";
                    // when
                    SocialType result = SocialType.findSocialTypeBy(stringGoogleType);
                    // then
                    assertThat(result).isEqualTo(SocialType.GOOGLE);
                }),
                DynamicTest.dynamicTest("애플 타입을 반환합니다.", () -> {
                    // given
                    String stringAppleType = "APPLE";
                    // when
                    SocialType result = SocialType.findSocialTypeBy(stringAppleType);
                    // then
                    assertThat(result).isEqualTo(SocialType.APPLE);
                }),
                DynamicTest.dynamicTest("존재하지 않는 소셜 타입입니다.", () -> {
                    // given
                    String stringNoneType = "GOGO";
                    // when & then
                    assertThatThrownBy(() -> SocialType.findSocialTypeBy(stringNoneType))
                            .isInstanceOf(SocialTypeNotFoundException.class)
                            .hasMessage("해당 소셜 타입은 존재하지 않습니다.");
                })
        );
    }



}