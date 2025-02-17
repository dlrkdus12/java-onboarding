package com.example.javaonboarding.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshtoken", timeToLive = 60 * 60 * 24 * 3) // 3일
public class RefreshToken {

    @Id
    private Long userId;

    // 만료된 access Token으로 refresh Token을 찾아와서 유효성을 검사
    @Indexed
    private String accessToken;

    private String refreshToken;
}