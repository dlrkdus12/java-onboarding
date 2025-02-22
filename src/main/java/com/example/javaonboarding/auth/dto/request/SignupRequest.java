package com.example.javaonboarding.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequest {

    @Schema(description = "User's username", example = "Bob")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Schema(description = "User's password", example = "securePassword123")
    private String password;

    @Schema(description = "User's nickname", example = "곰돌이")
    private String nickname;

}
