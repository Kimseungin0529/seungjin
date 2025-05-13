package com.project.doongdoong.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutDto {
    @NotBlank(message = "refreshToken이 비어 있습니다.")
    private String refreshToken;

}
