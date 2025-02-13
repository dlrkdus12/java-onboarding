package com.example.javaonboarding.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Token ErrorCode
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),

    // Sign up
    EXIST_USERNAME(HttpStatus.BAD_REQUEST, "존재하는 유저네임입니다."),
    // Sign in
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 조회에 실패했습니다."),
    SIGN_IN_ERROR(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }


}
