package com.project.doongdoong.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInformationResponseDto {
    private String nickname;

    private String email;

    private String socialType;

    private Long analysisCount;


    @Builder
    private UserInformationResponseDto(String nickname, String email, String socialType, Long analysisCount) {
        this.nickname = nickname;
        this.email = email;
        this.socialType = socialType;
        this.analysisCount = analysisCount;
    }

    public static UserInformationResponseDto of(String nickname, String email, String socialType, Long analysisCount) {
        return UserInformationResponseDto.builder()
                .nickname(nickname)
                .email(email)
                .socialType(socialType)
                .analysisCount(analysisCount)
                .build();
    }
}
