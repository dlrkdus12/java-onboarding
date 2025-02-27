package com.example.javaonboarding.auth.controller;

import com.example.javaonboarding.auth.dto.request.SigninRequest;
import com.example.javaonboarding.auth.dto.request.SignupRequest;
import com.example.javaonboarding.auth.dto.response.SigninResponse;
import com.example.javaonboarding.auth.dto.response.SignupResponse;
import com.example.javaonboarding.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API", description = "사용자 인증 APIs (회원가입 & 로그인)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자의 이름, 패스워드, 닉네임 정보 등록")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "사용자의 액세스 토큰을 쿠키, 헤더, 바디로 반환")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        SigninResponse token = authService.signin(signinRequest);

        Cookie cookie = new Cookie("Bearer", token.getAccessToken());
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
