package com.project.doongdoong.global.exception;

import static com.project.doongdoong.global.exception.ErrorType.Unauthorized.*;

public class MissingSocialTypeClaimException extends CustomException.UnauthorizedException {
    public MissingSocialTypeClaimException() {
        super(UNAUTHORIZED_SOCIAL_TYPE_CLAIM, "소셜 타입 정보가 없는 토큰입니다.");
    }
}
