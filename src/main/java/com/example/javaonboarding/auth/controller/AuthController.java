package com.example.javaonboarding.auth.controller;

import com.example.javaonboarding.auth.dto.request.SigninRequest;
import com.example.javaonboarding.auth.dto.request.SignupRequest;
import com.example.javaonboarding.auth.dto.response.SigninResponse;
import com.example.javaonboarding.auth.dto.response.SignupResponse;
import com.example.javaonboarding.auth.service.AuthService;
import com.example.javaonboarding.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        SigninResponse token = authService.signin(signinRequest);

        Cookie cookie = new Cookie("Bearer", token.getAccessToken());
        log.info(cookie.getValue());
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token.getAccessToken());
        SigninResponse signinResponse = new SigninResponse(token.getAccessToken());
        return ResponseEntity.ok()
                .headers(headers)
                .body(signinResponse);
    }
}
