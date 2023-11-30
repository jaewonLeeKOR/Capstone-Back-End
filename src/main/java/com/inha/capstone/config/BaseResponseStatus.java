package com.inha.capstone.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK,"요청에 성공하였습니다."),
  
    PERMISSION_DENIED(false, HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."),
    INVALID_PARAMETER(false, HttpStatus.BAD_REQUEST, "필수파라미터가 누락됐습니다."),
    DUPLICATED_USER(false, HttpStatus.CONFLICT, "중복된 회원입니다."),
    NOT_EXIST_USER(false, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_EXIST_APPLICATION(false,HttpStatus.NOT_FOUND, "존재하지 않는 애플리케이션입니다."),
    CONVERT_MULTIPART_FILE_FAILED(false, HttpStatus.SERVICE_UNAVAILABLE, "MultiPartFile 변환 실패"),
    INCORRECT_APPLICATIONID(false, HttpStatus.NO_CONTENT,"ApplicationId 는 0이 될 수 없습니다."),
    CANNOT_CREATE_FILE(false, HttpStatus.CONFLICT,"서버상에 임시파일을 생성할 수 없음"),
    NOT_EXIST_FILE(false, HttpStatus.NO_CONTENT, "존재하지 않는 파일입니다."),
    CRAWLER_ERROR(false, HttpStatus.BAD_REQUEST, "크롤러에서 에러가 발생했습니다."),
    MAKE_HTML_ERROR(false, HttpStatus.BAD_REQUEST, "make html에서 에러가 발생했습니다."),
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
