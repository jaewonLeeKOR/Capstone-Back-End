package com.inha.capstone.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.inha.capstone.config.BaseResponseStatus.SUCCESS;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.httpStatus = SUCCESS.getHttpStatus();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    public BaseResponse() {
        this.isSuccess = SUCCESS.isSuccess();
        this.httpStatus = SUCCESS.getHttpStatus();
        this.message = SUCCESS.getMessage();
        this.result = null;
    }

    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.httpStatus = status.getHttpStatus();
        this.message = status.getMessage();
    }

    public BaseResponse(Exception e){
        this.isSuccess = false;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = e.getMessage();
    }

}
