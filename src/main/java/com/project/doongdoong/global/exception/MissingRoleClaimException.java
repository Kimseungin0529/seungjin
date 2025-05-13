package com.project.doongdoong.global.exception;

import static com.project.doongdoong.global.exception.ErrorType.Unauthorized.*;

public class MissingRoleClaimException extends CustomException.UnauthorizedException {

    public MissingRoleClaimException() {
        super(UNAUTHORIZED_ROLE_CLAIM, "권한 정보가 없는 토큰입니다.");
    }
}
