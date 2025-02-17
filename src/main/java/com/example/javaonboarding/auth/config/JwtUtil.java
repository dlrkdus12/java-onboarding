package com.example.javaonboarding.auth.config;

import com.example.javaonboarding.auth.entity.RefreshToken;
import com.example.javaonboarding.auth.enums.UserRole;
import com.example.javaonboarding.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer "; // 접두사 - Bearer ~
    private static final long ACCESS_TOKEN_VALIDITY = 10 * 1000L; // 토큰 유효시간 - 10초
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 3; // 3 days

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 클래스 초기화 메서드
     * secretKey 를 Base64로 디코딩하여 HMAC 서명에 사용할 키를 초기화
     * JWT 생성 및 검증을 위한 키 설정을 수행
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long userId, String username, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("username", username)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_VALIDITY))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId, String accessToken) {
        Date date = new Date();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_VALIDITY))
                .setIssuedAt(date) // 발급일
                .signWith(key) // 암호화 알고리즘
                .compact();
        RefreshToken token = new RefreshToken(userId, accessToken, refreshToken);
        refreshTokenRepository.save(token);
        return refreshToken;
    }

    // 유효한 엑세스 토큰인지 검증
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser() // Jwts.parser() 파서를 생성한다. 생성된 파서로 토큰을 파싱하고 서명을 확인할 수 있다.)
                    .setSigningKey(secretKey) // 비밀키를 설정하여 파싱한다.
                    .parseClaimsJws(token);  // 주어진 토큰을 파싱하여 Claims 객체를 얻는다.

            // 만료 시간 체크
            Date expiration = claims.getBody().getExpiration();
            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            log.error("❌ 만료된 토큰: " + e.getMessage());

        } catch (Exception e) {
            log.error("❌ 토큰 검증 실패: " + e.getMessage());
        }
        return  true;
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new IllegalArgumentException("Token not found or invalid format");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Bearer".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}