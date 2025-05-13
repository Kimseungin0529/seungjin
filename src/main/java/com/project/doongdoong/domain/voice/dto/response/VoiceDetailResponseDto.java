package com.project.doongdoong.domain.voice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceDetailResponseDto {
    private String accessUrl;

    @Builder
    private VoiceDetailResponseDto(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public static VoiceDetailResponseDto of(String accessUrl){
        return VoiceDetailResponseDto.builder()
                .accessUrl(accessUrl)
                .build();
    }
}



