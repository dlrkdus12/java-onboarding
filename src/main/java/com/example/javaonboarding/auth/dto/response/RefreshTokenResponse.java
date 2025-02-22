package com.example.javaonboarding.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResponse {

    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String refreshToken;
}
