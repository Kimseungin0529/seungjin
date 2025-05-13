package com.project.doongdoong.global.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class OAuthTokenDto {

    @NotBlank(message = "소셜 id이가 존재하지 않습니다.")
    private String socialId;

    @NotBlank(message = "닉네임이 공백입니다.")
    private String nickname;

    @Email(message = "이메일 형식이 아닙니다.") // option으로 하기
    private String email;

    @NotBlank(message = "OAuth 타입이 공백입니다.")
    @Pattern(regexp = "[A-Z]+", message = "알파벳 대문자로 입력해주세요.")
    private String socialType;

}
