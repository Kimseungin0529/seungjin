package com.project.doongdoong.domain.voice.exception;

import com.project.doongdoong.global.exception.CustomException;

import static com.project.doongdoong.global.exception.ErrorType.NotFound.EXTENSION_NOT_FOUND;

public class NotFoundFileExtension extends CustomException.NotFoundException {

    public NotFoundFileExtension(String extension) {
        super(EXTENSION_NOT_FOUND, String.format("%s 확장자를 찾을 수 없습니다.", extension));
    }
}
