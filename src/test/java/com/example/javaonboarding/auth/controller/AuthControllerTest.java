package com.example.javaonboarding.auth.controller;

import com.example.javaonboarding.auth.dto.request.SigninRequest;
import com.example.javaonboarding.auth.dto.response.SigninResponse;
import com.example.javaonboarding.auth.entity.User;
import com.example.javaonboarding.auth.enums.UserRole;
import com.example.javaonboarding.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = OncePerRequestFilter.class)})
@Slf4j
public class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void signin() throws Exception {
        // Given
        User user = new User("user1", "12341234", "Mentos", UserRole.ROLE_USER);
        log.info(objectMapper.writeValueAsString(user));

        SigninRequest signinRequest = new SigninRequest(user.getUsername(), user.getPassword());
        log.info("요청 JSON: {}", objectMapper.writeValueAsString(signinRequest));

        SigninResponse signinResponse = new SigninResponse("myAccessToken");

        given(authService.signin(any())).willReturn(signinResponse);

        // When & Then
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }


}