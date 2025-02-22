package com.example.javaonboarding.auth.dto.response;

import com.example.javaonboarding.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SignupResponse {

    @Schema(description = "User's username", example = "Bob")
    private final String username;

    @Schema(description = "User's nickname", example = "곰돌이")
    private final String nickname;

    @Schema(description = "User's Role", example = "ROLE_USER")
    private final List<Map<String, String>> authorities;

    public SignupResponse(User saveUser) {
        this.username = saveUser.getUsername();
        this.nickname = saveUser.getNickname();
        this.authorities = List.of(Map.of("authorityName", saveUser.getUserRole().name()));
    }
}
