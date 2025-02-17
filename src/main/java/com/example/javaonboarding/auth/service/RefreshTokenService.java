package com.example.javaonboarding.auth.service;

import com.example.javaonboarding.auth.config.JwtUtil;
import com.example.javaonboarding.auth.entity.RefreshToken;
import com.example.javaonboarding.auth.entity.User;
import com.example.javaonboarding.auth.repository.RefreshTokenRepository;
import com.example.javaonboarding.auth.repository.UserRepository;
import com.example.javaonboarding.common.enums.ErrorCode;
import com.example.javaonboarding.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    public RefreshToken getRefreshToken(String accessToken){
        return refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    @Transactional
    public void removeRefreshToken(String accessToken){
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
    }

    @Transactional
    public String reCreateAccessToken(String originAccessToken, RefreshToken refreshToken){
        Long userId = refreshToken.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getUserRole());
        String newAccessToken = accessToken.replace("Bearer ", "");

        removeRefreshToken(originAccessToken);
        refreshTokenRepository.save(new RefreshToken(userId, newAccessToken, refreshToken.getRefreshToken()));
        return newAccessToken;
    }
}