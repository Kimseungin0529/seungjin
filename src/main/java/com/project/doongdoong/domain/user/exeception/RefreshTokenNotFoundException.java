package com.project.doongdoong.domain.user.exeception;

import com.project.doongdoong.global.exception.CustomException;

import static com.project.doongdoong.global.exception.ErrorType.NotFound.REFRESH_TOKEN_NOT_FOUND;

public class RefreshTokenNotFoundException extends CustomException.NotFoundException {
    public RefreshTokenNotFoundException() {
        super(REFRESH_TOKEN_NOT_FOUND, "refresh Token 이 존재하지 않아 토큰 갱신에 실패했습니다.");
    }
}
