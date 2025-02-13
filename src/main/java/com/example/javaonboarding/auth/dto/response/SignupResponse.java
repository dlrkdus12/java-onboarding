package com.example.javaonboarding.auth.dto.response;

import com.example.javaonboarding.auth.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SignupResponse {
    private final String username;
    private final String nickname;
    private final List<Map<String, String>> authorities;

    public SignupResponse(User saveUser) {
        this.username = saveUser.getUsername();
        this.nickname = saveUser.getNickname();
        this.authorities = List.of(Map.of("authorityName", saveUser.getUserRole().name()));
    }
}
