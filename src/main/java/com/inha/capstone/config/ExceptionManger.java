package com.inha.capstone.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManger {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> baseExceptionHandler(BaseResponseStatus status) {
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse(status));

    }
}
