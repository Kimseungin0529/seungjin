package com.project.doongdoong.domain.user.controller;

import com.project.doongdoong.domain.user.dto.UserInformationResponseDto;
import com.project.doongdoong.global.dto.request.LogoutDto;
import com.project.doongdoong.global.dto.request.OAuthTokenDto;
import com.project.doongdoong.global.dto.request.ReissueDto;
import com.project.doongdoong.global.dto.response.TokenDto;
import com.project.doongdoong.module.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("사용자 로그인을 진행합니다. 회원 정보가 없다면 회원 정보 저장 후, 로그인 진행합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userSignIn() throws Exception {
        // given
        String socialId = "123456";
        String nickname = "진이";
        String email = "test@naver.com";
        String socialType = "APPLE";
        OAuthTokenDto request = new OAuthTokenDto(socialId, nickname, email, socialType);

        String accessToken = "Bearer accessToken.xx.xx";
        String refreshToken = "Bearer refreshToken.xx.xx";
        TokenDto response = TokenDto.of(accessToken, refreshToken);

        given(userService.checkRegistration(any(OAuthTokenDto.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/login-oauth")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));

    }

    @Test
    @DisplayName("사용자 로그인을 진행합니다. 회원 정보가 없다면 회원 정보 저장 후, 로그인 진행합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userSignInExceptionOfValid() throws Exception {

        // given
        String socialId = "  ";
        String nickname = "    ";
        String email = "test24naver.com";
        String socialType = " ";
        OAuthTokenDto request = new OAuthTokenDto(socialId, nickname, email, socialType);

        String accessToken = "Bearer accessToken.xx.xx";
        String refreshToken = "Bearer refreshToken.xx.xx";
        TokenDto response = TokenDto.of(accessToken, refreshToken);

        given(userService.checkRegistration(any(OAuthTokenDto.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/login-oauth")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.message").value("잘못된 입력 형식이 존재합니다."))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details",
                        containsInAnyOrder("소셜 id이가 존재하지 않습니다.", "닉네임이 공백입니다.",
                                "이메일 형식이 아닙니다.", "OAuth 타입이 공백입니다.", "알파벳 대문자로 입력해주세요.")
                ));


    }

    @Test
    @DisplayName("사용자가 로그아웃합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userLogout() throws Exception {
        // given
        String accessToken = "Bearer aaaa.bbbb.cccc";
        String refreshToken = "Bearer aaa.bbb.ccc";
        LogoutDto request = new LogoutDto(refreshToken);

        String response = "logout success";

        willDoNothing().given(userService).logout(any(LogoutDto.class), anyString());

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/logout-oauth")
                                .with(csrf())
                                .header("Authorization", accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.data").value(response));

    }

    @Test
    @DisplayName("로그아웃에는 refresh token 정보가 필요합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userLogoutExceptionOfLogoutDto() throws Exception {
        // given
        String accessToken = "Bearer aaaa.bbbb.cccc";
        String refreshToken = " ";
        LogoutDto request = new LogoutDto(refreshToken);


        willDoNothing().given(userService).logout(any(LogoutDto.class), anyString());

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/logout-oauth")
                                .with(csrf())
                                .header("Authorization", accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.message").value("잘못된 입력 형식이 존재합니다."))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details",
                        containsInAnyOrder("refreshToken이 비어 있습니다.")
                ));

    }

    @Test
    @DisplayName("사용자가 토큰을 재발급합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userReissue() throws Exception {
        // given
        String refreshToken = "Bearer aaa.bbb.ccc";
        ReissueDto request = new ReissueDto(refreshToken);

        String newAccessToken = "Bearer aaaa.bbbb.cccc";
        String newRefreshToken = "Bearer qqqqq.wwww.eee";
        TokenDto response = TokenDto.of(newAccessToken, newRefreshToken);

        given(userService.reissue(any(ReissueDto.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/reissue")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.accessToken").value(response.getAccessToken()))
                .andExpect(jsonPath("$.data.refreshToken").value(response.getRefreshToken()));

    }

    @Test
    @DisplayName("토큰 재발급에는 refresh token 정보가 필수입니다.")
    @WithMockUser(username = "123456_APPLE")
    void userReissueExceptionOf() throws Exception {
        // given
        String refreshToken = " ";
        ReissueDto request = new ReissueDto(refreshToken);


        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/user/reissue")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.message").value("잘못된 입력 형식이 존재합니다."))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details",
                        containsInAnyOrder("refreshToken이 비어 있습니다.")
                ));

    }


    @Test
    @DisplayName("마이페이지 정보를 조회합니다.")
    @WithMockUser(username = "123456_APPLE")
    void userMyPage() throws Exception {
        // given
        String nickname = "진22";
        String email = "hong@example.com";
        String socialType = "APPLE";
        Long analysisCount = 5L;
        UserInformationResponseDto response = UserInformationResponseDto.builder()
                .nickname(nickname)
                .email(email)
                .socialType(socialType)
                .analysisCount(analysisCount)
                .build();

        given(userService.getMyPage(any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/user/my-page")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.socialType").value(response.getSocialType()))
                .andExpect(jsonPath("$.data.analysisCount").value(response.getAnalysisCount()));

    }

    @Test
    @DisplayName("서버 ping 확인 API")
    @WithMockUser(username = "123456_APPLE")
    void testPing() throws Exception {
        // given
        String response = "ping";
        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/user/ping")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").value(response));

    }



}