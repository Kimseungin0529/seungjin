package com.project.doongdoong.domain.user.service.spring;

import com.project.doongdoong.domain.user.dto.UserInformationResponseDto;
import com.project.doongdoong.domain.user.exeception.RefreshTokenNotFoundException;
import com.project.doongdoong.domain.user.model.User;
import com.project.doongdoong.domain.user.repository.UserRepository;
import com.project.doongdoong.domain.user.service.UserService;
import com.project.doongdoong.global.common.BlackAccessToken;
import com.project.doongdoong.global.dto.request.LogoutDto;
import com.project.doongdoong.global.dto.request.OAuthTokenDto;
import com.project.doongdoong.global.dto.request.ReissueDto;
import com.project.doongdoong.global.dto.response.TokenDto;
import com.project.doongdoong.global.repositoty.BlackAccessTokenRepository;
import com.project.doongdoong.global.repositoty.RefreshTokenRepository;
import com.project.doongdoong.module.IntegrationSupportTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static com.project.doongdoong.domain.user.model.SocialType.APPLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceImplSpringTest extends IntegrationSupportTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    BlackAccessTokenRepository blackAccessTokenRepository;


    @DisplayName("등록 여부에 따라 로그인 또는 회원가입 처리합니다.")
    @Test
    void checkRegistration() {

        // given
        String socialId = "123456789";
        String nickname = "testName";
        String email = "test@test.com";
        String socialType = "KAKAO";
        OAuthTokenDto request = new OAuthTokenDto(socialId, nickname, email, socialType);

        // when
        TokenDto result = userService.checkRegistration(request);

        // then
        assertThat(result)
                .extracting(TokenDto::getAccessToken, TokenDto::getRefreshToken)
                .allSatisfy(
                        token -> assertThat(token).isInstanceOf(String.class)
                );
    }


    @TestFactory
    @DisplayName("사용자 로그인/회원가입 이후, 사용자 시나리오")
    Collection<DynamicTest> scenario_IntegrationTest() {
        // given
        String socialId = "123456789";
        String nickname = "testName";
        String email = "test@test.com";
        String socialType = "KAKAO";

        OAuthTokenDto request = new OAuthTokenDto(socialId, nickname, email, socialType);
        TokenDto tokenDto = userService.checkRegistration(request);

        // when & then
        return List.of(
                DynamicTest.dynamicTest("인증/인가 토큰을 재발급합니다.", () -> {
                    // given
                    ReissueDto reissueDto = new ReissueDto(tokenDto.getRefreshToken());
                    // when
                    TokenDto newTokenDto = userService.reissue(reissueDto);
                    // then
                    assertThat(newTokenDto)
                            .extracting(TokenDto::getAccessToken)
                            .isNotEqualTo(tokenDto.getAccessToken());
                }),
                DynamicTest.dynamicTest("인증/인가 토큰을 재발급이 실패합니다..", () -> {
                    // given
                    ReissueDto reissueDto = new ReissueDto("Bearer ax2ha.bant7SQs19.cahzZ1oXQ4");
                    // when & then
                    assertThatThrownBy(() -> userService.reissue(reissueDto))
                            .isInstanceOf(RefreshTokenNotFoundException.class)
                                    .hasMessage("refresh Token 이 존재하지 않아 토큰 갱신에 실패했습니다.");
                }),
                DynamicTest.dynamicTest("사용자 로그아웃 처리합니다.", () -> {
                    // given
                    LogoutDto logoutDto = new LogoutDto(tokenDto.getRefreshToken());

                    // when
                    userService.logout(logoutDto, tokenDto.getAccessToken());

                    // then
                    assertThat(refreshTokenRepository
                            .findByRefreshToken(tokenDto.getRefreshToken()).isEmpty()
                    ).isTrue();

                    assertThat(blackAccessTokenRepository
                            .findByUniqueId(BlackAccessToken.generateUniqueKeyWith(socialId, socialType))
                    ).isPresent();

                })
        );

    }


    @DisplayName("사용자의 상세 정보를 반환합니다.")
    @Test
    void getMyPage() {
        // given
        User user = User.builder()
                .socialId("123456789")
                .socialType(APPLE)
                .email("exam@test.com")
                .nickname("testName")
                .build();

        User savedUser = userRepository.save(user);
        String uniqueValue = savedUser.getSocialId() + "_" + savedUser.getSocialType().getDescription();

        // when
        UserInformationResponseDto result = userService.getMyPage(uniqueValue);

        // then
        assertThat(result)
                .extracting(UserInformationResponseDto::getNickname,
                        UserInformationResponseDto::getEmail,
                        UserInformationResponseDto::getSocialType,
                        UserInformationResponseDto::getAnalysisCount)
                .contains("testName", "exam@test.com", APPLE.getDescription(), 0L);

    }


}