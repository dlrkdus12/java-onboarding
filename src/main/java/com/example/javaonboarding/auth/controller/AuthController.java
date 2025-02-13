package com.example.javaonboarding.auth.controller;

import com.example.javaonboarding.auth.dto.request.SigninRequest;
import com.example.javaonboarding.auth.dto.response.SigninResponse;
import com.example.javaonboarding.auth.dto.request.SignupRequest;
import com.example.javaonboarding.auth.dto.response.SignupResponse;
import com.example.javaonboarding.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        String token = authService.signin(signinRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        SigninResponse response = new SigninResponse(token);
        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

}
