package com.project.doongdoong.domain.user.controller;

import com.project.doongdoong.domain.user.dto.UserInformationResponseDto;
import com.project.doongdoong.domain.user.service.UserService;
import com.project.doongdoong.global.annotation.CurrentUser;
import com.project.doongdoong.global.common.ApiResponse;
import com.project.doongdoong.global.dto.request.LogoutDto;
import com.project.doongdoong.global.dto.request.OAuthTokenDto;
import com.project.doongdoong.global.dto.request.ReissueDto;
import com.project.doongdoong.global.dto.response.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/ping")
    public ApiResponse<?> testPing() {
        return ApiResponse.of(HttpStatus.OK, null, "ping");
    }


    @PostMapping("/login-oauth")
    public ApiResponse<TokenDto> userSignIn(@Valid @RequestBody OAuthTokenDto oAuthTokenInfo) {
        TokenDto tokenInfoResponse = userService.checkRegistration(oAuthTokenInfo);

        return ApiResponse.of(HttpStatus.OK, null, tokenInfoResponse);
    }


    @PostMapping("/logout-oauth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> userLogout(@Valid @RequestBody LogoutDto logoutDto,
                                     @RequestHeader("Authorization") String accessToken) {
        userService.logout(logoutDto, accessToken);
        return ApiResponse.of(HttpStatus.NO_CONTENT, null, "logout success");
    }


    @PostMapping("/reissue")
    public ApiResponse<TokenDto> userReissue(@Valid @RequestBody ReissueDto reissueDto) {

        return ApiResponse.of(HttpStatus.OK, null, userService.reissue(reissueDto));
    }

    @GetMapping("/my-page")
    public ApiResponse<UserInformationResponseDto> userMyPage(@CurrentUser String uniqueValue) {

        return ApiResponse.of(HttpStatus.OK, null, userService.getMyPage(uniqueValue));
    }

}
