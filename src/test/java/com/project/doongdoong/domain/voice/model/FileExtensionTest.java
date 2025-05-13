package com.project.doongdoong.domain.voice.model;

import com.project.doongdoong.domain.voice.exception.NotFoundFileExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileExtensionTest {

    @ParameterizedTest
    @ValueSource(strings = {"file.mp3", "file.m4a", "file.wav", "file.MP3", "file.M4a", "file.wAV"})
    @DisplayName("대소문자 구분 없이 올바른 확장자를 가진 파일명을 입력하면 대응하는 MIME 타입을 반환한다")
    void returns_mime_type_when_valid_extension(String filename) {
        // when
        String mimeType = FileExtension.getMineTypeFrom(filename);

        //then
        assertThat(mimeType).isIn("audio/mpeg", "audio/mp4", "audio/wav");
    }


    @Test
    @DisplayName("지원하지 않는 확장자를 입력하면 예외를 던진다")
    void throws_exception_when_unsupported_extension() {
        // given
        String filename = "file.ogg";

        // when & then
        assertThatThrownBy(() -> FileExtension.getMineTypeFrom(filename))
                .isInstanceOf(NotFoundFileExtension.class)
                .hasMessageContaining("OGG 확장자를 찾을 수 없습니다.");
    }


}