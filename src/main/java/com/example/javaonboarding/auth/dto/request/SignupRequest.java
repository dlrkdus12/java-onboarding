package com.example.javaonboarding.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    private String nickname;

    public SignupRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
