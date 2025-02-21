package com.example.javaonboarding.auth.config;

import com.example.javaonboarding.auth.enums.UserRole;
import com.example.javaonboarding.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import io.jsonwebtoken.Claims;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secretKey = "my-secret-key-for-jwt-signing-my-secret-key-for-jwt-signing"; // 테스트용 키

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(refreshTokenRepository);
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        ReflectionTestUtils.setField(jwtUtil, "secretKey", encodedKey);
        jwtUtil.init();
    }

    @Test
    void createAccessToken_ShouldReturnValidToken() {
        String token = jwtUtil.createAccessToken(1L, "testUser", UserRole.ROLE_USER);
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    void createRefreshToken_ShouldReturnValidToken() {
        String token = jwtUtil.createRefreshToken(1L, "someAccessToken");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void substringToken_ShouldExtractTokenWithoutBearer() {
        String token = "Bearer myToken123";
        assertEquals("myToken123", jwtUtil.substringToken(token));
    }

    @Test
    void substringToken_ShouldThrowException_WhenInvalidFormat() {
        String token = "InvalidToken";
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.substringToken(token));
    }

    @Test
    void extractClaims_ShouldReturnCorrectClaims() {
        String token = jwtUtil.createAccessToken(1L, "testUser", UserRole.ROLE_USER);
        String extractedToken = jwtUtil.substringToken(token);

        Claims claims = jwtUtil.extractClaims(extractedToken);
        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals("ROLE_USER", claims.get("userRole"));
    }

    @Test
    void verifyToken_ShouldReturnCorrectClaims() {
        String token = jwtUtil.createAccessToken(1L, "testUser", UserRole.ROLE_USER);
        String extractedToken = jwtUtil.substringToken(token);

        Claims claims = jwtUtil.verifyToken(extractedToken);

        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals("ROLE_USER", claims.get("userRole"));
    }

    @Test
    void resolveToken_ShouldReturnToken_WhenCookieIsPresent() {
        // Given: mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie("Bearer", "myToken123");
        Cookie[] cookies = { cookie };

        // 설정: getCookies 메서드가 위의 cookies 배열을 반환하도록 설정
        when(request.getCookies()).thenReturn(cookies);

        // When: resolveToken 메서드 호출
        String token = jwtUtil.resolveToken(request);

        // Then: "Bearer" 쿠키의 값이 반환되어야 한다
        assertNotNull(token);
        assertEquals("myToken123", token);
    }

    @Test
    void resolveToken_ShouldReturnNull_WhenNoBearerCookieIsPresent() {
        // Given: mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie("SomeOtherCookie", "otherValue");
        Cookie[] cookies = { cookie };

        // 설정: getCookies 메서드가 위의 cookies 배열을 반환하도록 설정
        when(request.getCookies()).thenReturn(cookies);

        // When: resolveToken 메서드 호출
        String token = jwtUtil.resolveToken(request);

        // Then: "Bearer" 쿠키가 없으면 null이 반환되어야 한다
        assertNull(token);
    }

}