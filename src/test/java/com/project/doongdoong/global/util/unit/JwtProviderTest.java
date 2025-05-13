package com.project.doongdoong.global.util.unit;

import com.project.doongdoong.domain.user.service.UserService;
import com.project.doongdoong.global.dto.request.LogoutDto;
import com.project.doongdoong.global.dto.response.TokenDto;
import com.project.doongdoong.global.util.JwtProvider;
import com.project.doongdoong.module.IntegrationSupportTest;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest extends IntegrationSupportTest {

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    UserService userService;

    @DisplayName("JWT 토큰을 생성합니다.")
    @Test
    void generateToken() {
        // given
        String socialId = "123462671";
        String socialType = "GOOGLE";
        List<String> roles = List.of("ROLE_USER");
        // when
        TokenDto result = jwtProvider.generateToken(socialId, socialType, roles);
        // then
        assertThat(result)
                .satisfies(
                        token -> {
                            assertThat(token.getAccessToken()).contains("Bearer ");
                            assertThat(token.getRefreshToken()).contains("Bearer ");
                        }
                );
    }

    @DisplayName("JWT 토큰 생성 이후, 검증 및 인증/인가 설정 시나리오")
    @TestFactory
    Collection<DynamicTest> generateAuthentication() {
        // given
        String socialId = "123462671";
        String socialType = "APPLE";
        List<String> roles = List.of("ROLE_USER");

        TokenDto tokenDto = jwtProvider.generateToken(socialId, socialType, roles);
        String accessToken = tokenDto.getAccessToken().replace("Bearer ", ""); //

        // when & then
        return List.of(
                DynamicTest.dynamicTest("인증/인가 권한 정보를 생성합니다.", () -> {
                    // when
                    Authentication authentication = jwtProvider.generateAuthentication(accessToken);
                    // then
                    assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);

                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    assertThat(userDetails)
                            .satisfies(user -> {
                                assertThat(user.getUsername()).contains(socialId)
                                        .contains(socialType);
                                assertThat(user.getAuthorities())
                                        .extracting(GrantedAuthority::getAuthority)
                                        .containsExactlyInAnyOrder("ROLE_USER");
                            });
                }),
                DynamicTest.dynamicTest("JWT 토큰이 올바른지 검증합니다.", () -> {
                    // when
                    boolean isValid = jwtProvider.validateToken(accessToken);
                    // then
                    assertThat(isValid).isTrue();
                }),
                DynamicTest.dynamicTest("잘못된 JWT 토큰으로 접근하는 경우, 인증 허용하지 않습니다.", () -> {
                    // given
                    String modulatedAccessToken = accessToken + "d2G";
                    // when & then
                    assertThatThrownBy(() -> jwtProvider.generateAuthentication(modulatedAccessToken))
                            .isInstanceOf(SignatureException.class);
                })
        );

    }

    @DisplayName("JWT 토큰 생성 이후, 블랙 토큰 확인 시나리오")
    @TestFactory
    Collection<DynamicTest> isBlackToken() {
        // given
        String socialId = "123462671";
        String socialType = "APPLE";
        List<String> roles = List.of("ROLE_USER");

        TokenDto tokenDto = jwtProvider.generateToken(socialId, socialType, roles);
        String accessToken = tokenDto.getAccessToken().replace("Bearer ", "");

        // when & then
        return List.of(
                DynamicTest.dynamicTest("생성된 JWT 토큰은 블랙 토큰이 아닙니다.", () -> {
                    // when
                    boolean isBlackToken = jwtProvider.isBlackToken(accessToken);

                    // then
                    assertThat(isBlackToken).isFalse();
                }),
                DynamicTest.dynamicTest("로그아웃에 사용된 JWT 토큰은 블랙 토큰입니다.", () -> {
                    //given
                    LogoutDto logoutDto = new LogoutDto(tokenDto.getRefreshToken());
                    userService.logout(logoutDto, tokenDto.getAccessToken());

                    // when
                    boolean isValid = jwtProvider.isBlackToken(accessToken);

                    // then
                    assertThat(isValid).isTrue();
                })
        );
    }


    @DisplayName("JWT 토큰 값 추출 시나리오")
    @TestFactory
    Collection<DynamicTest> extractValueFromToken() {
        // given
        String socialId = "123462671";
        String socialType = "APPLE";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN" );

        TokenDto tokenDto = jwtProvider.generateToken(socialId, socialType, roles);
        String accessToken = tokenDto.getAccessToken();

        // when & then
        return List.of(
                DynamicTest.dynamicTest("JWT 토큰에서 socialId 를 추출합니다." , () -> {
                    // when
                    String result = jwtProvider.extractSocialId(accessToken);

                    // then
                    assertThat(result).isEqualTo(socialId);
                }),
                DynamicTest.dynamicTest("JWT 토큰에서 social type 을 추출합니다.",() -> {
                    // when
                    String result = jwtProvider.extractSocialType(accessToken);

                    // then
                    assertThat(result).isEqualTo(socialType);
                }),
                DynamicTest.dynamicTest("JWT 토큰에서 권한을 추출합니다.", () -> {
                    // when
                    List<String> result = jwtProvider.extractRole(accessToken);

                    // then
                    assertThat(result).isEqualTo(roles);
                })
        );
    }


}