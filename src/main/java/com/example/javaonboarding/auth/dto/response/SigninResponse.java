package com.example.javaonboarding.auth.dto.response;

import lombok.Getter;

@Getter
public class SigninResponse {
    private final String accessToken;

    public SigninResponse(String accessToken) {
        this.accessToken = accessToken; // "Bearer " 제거
    }
}