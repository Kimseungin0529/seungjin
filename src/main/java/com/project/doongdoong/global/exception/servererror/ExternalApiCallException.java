package com.project.doongdoong.global.exception.servererror;

import com.project.doongdoong.global.exception.CustomException;
import com.project.doongdoong.global.exception.ErrorType;

import static com.project.doongdoong.global.exception.ErrorType.ServerError.EXTERNAL_SERVER_ERROR;

public class ExternalApiCallException extends CustomException.ServerErrorException {
    public ExternalApiCallException() {
        super(EXTERNAL_SERVER_ERROR, "외부 API 가 정상 처리되지 않았습니다. 외부 서버를 사용할 수 없습니다.");

    }
    public ExternalApiCallException(String message) {
        super(EXTERNAL_SERVER_ERROR, "외부 API 가 정상 처리되지 않았습니다. Lambda API 호출 실패 : " + message);

    }
}
