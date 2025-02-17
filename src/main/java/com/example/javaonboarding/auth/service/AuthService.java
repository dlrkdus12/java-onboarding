package com.example.javaonboarding.auth.service;

import com.example.javaonboarding.auth.config.JwtUtil;
import com.example.javaonboarding.auth.dto.request.SigninRequest;
import com.example.javaonboarding.auth.dto.request.SignupRequest;
import com.example.javaonboarding.auth.dto.response.SigninResponse;
import com.example.javaonboarding.auth.dto.response.SignupResponse;
import com.example.javaonboarding.auth.entity.User;
import com.example.javaonboarding.auth.enums.UserRole;
import com.example.javaonboarding.auth.repository.UserRepository;
import com.example.javaonboarding.common.enums.ErrorCode;
import com.example.javaonboarding.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new CustomException(ErrorCode.EXIST_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User newUser = new User(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getNickname(),
                UserRole.ROLE_USER);

        User savedUser = userRepository.save(newUser);
        return new SignupResponse(savedUser);
    }

    @Transactional
    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByUsername(signinRequest.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.SIGN_IN_ERROR);
        }

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getUserRole());
        SigninResponse signinResponse = new SigninResponse(accessToken);

        // 리프레시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), signinResponse.getAccessToken());

        log.info("userId : " + user.getId() + ", Login Success");
        log.info("accessToken : " + signinResponse.getAccessToken());
        log.info("refreshToken : " + refreshToken);

        return signinResponse;
    }

}
