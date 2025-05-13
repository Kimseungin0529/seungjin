package com.project.doongdoong.global.exception;


public interface ErrorType {
    int getCode();

    enum BadRequest implements ErrorType {
        BAD_REQUEST_DEFAULT(0),
        FILE_EMPTY(1),
        NO_MATCHING_QUESTION(2),
        COUNSEL_TYPE_WRONG(3);

        private final int errorCode;

        BadRequest(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }

    enum Unauthorized implements ErrorType {
        UNAUTHORIZED_DEFAULT(1000),
        LOGIN_FAILED(1001),
        UNAUTHORIZED_COUNSEL(1002),
        UNAUTHORIZED_ROLE_CLAIM(1003),
        UNAUTHORIZED_SOCIAL_TYPE_CLAIM(1004)
        ;

        private final int errorCode;

        Unauthorized(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }

    enum Forbidden implements ErrorType {
        FORBIDDEN_DEFAULT(3000),
        TOKEN_INFO_FORBIDDEN(3001),
        ACCESS_DENIED(3100),
        ANALYSIS_ACCESS_DENIED(3102);

        private final int errorCode;

        Forbidden(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }

    enum NotFound implements ErrorType {
        NOT_FOUND_DEFAULT(4000),
        SOCIAL_TYPE_NOT_FOUND(4001),
        REFRESH_TOKEN_NOT_FOUND(4002),
        IMAGE_URL_NOT_FOUND(4003),
        VOICE_NOT_FOUND(4004),
        ANALYSIS_NOT_FOUND(4005),
        USER_NOT_FOUND(4006),
        QUESTION_NOT_FOUND(4007),
        ANSWER_NOT_FOUND(4008),
        ALL_ANSWER_NOT_FOUND(4009),
        COUNSEL_NOT_FOUND(4010),
        EXTENSION_NOT_FOUND(4011),
        ;
        private final int errorCode;

        NotFound(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }

    enum Conflict implements ErrorType {
        CONFLICT_DEFAULT(9000),
        ANSWER_ALREADY_CREATED(9001),
        COUNSEL_ALREADY_EXIST(9002),
        ANALYSIS_ALREADY_ANALYZE(9003);

        private final int errorCode;

        Conflict(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }

    enum ServerError implements ErrorType {
        SERVER_ERROR_DEFAULT(5000),
        FILE_UPLOAD_FAIL(5001),
        FILE_DELETE_FAIL(5002),
        EXTERNAL_SERVER_ERROR(5003)
        ;

        private final int errorCode;

        ServerError(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getCode() {
            return this.errorCode;
        }
    }
}

