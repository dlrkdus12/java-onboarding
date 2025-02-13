package com.example.javaonboarding.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequest {

    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    public SigninRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
