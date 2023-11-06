package com.inha.capstone.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK,"요청에 성공하였습니다."),
    PERMISSION_DENIED(false, HttpStatus.FORBIDDEN, "권한이 없는 요청입니다.")
    ;
    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, HttpStatus httpStatus, String message) {
        this.isSuccess = isSuccess;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
