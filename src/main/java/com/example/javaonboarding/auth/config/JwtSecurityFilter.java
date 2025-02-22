package com.example.javaonboarding.auth.config;

import com.example.javaonboarding.auth.dto.AuthUser;
import com.example.javaonboarding.auth.entity.RefreshToken;
import com.example.javaonboarding.auth.enums.UserRole;
import com.example.javaonboarding.auth.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpRequest,
            @NonNull HttpServletResponse httpResponse,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        String requestURI = httpRequest.getRequestURI(); // httpRequest 사용!

        log.info("Request URI: {}", requestURI);

        // Swagger 관련 요청 필터 제외
        if (requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/api-docs")
                || requestURI.equals("/swagger-ui.html")) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        String authorizationHeader = httpRequest.getHeader("Authorization");
        // 액세스 토큰 검증
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String jwt = jwtUtil.substringToken(authorizationHeader);
            log.info("🐝Header JWT token: {}", jwt);

            String accessToken = jwtUtil.resolveToken(httpRequest);
            log.info("🐝Cookie Access token: {}", accessToken);

            // 액세스 토큰 만료 검증
            try {
                Claims claims = jwtUtil.verifyToken(accessToken); // ✅ 유효하면 claims 반환, 만료되면 예외 발생
                log.info("claims : {}",claims.toString());

                Long userId = Long.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);
                UserRole userRole = UserRole.of(claims.get("userRole", String.class));

                AuthUser authUser = new AuthUser(userId, username, userRole);
                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (ExpiredJwtException c) {
                log.info("️🦄엑세스 토큰 만료 됨");
                log.error("Expired JWT token, 만료된 JWT token 입니다.", c);

                RefreshToken refreshToken = refreshTokenService.getRefreshToken(accessToken);
                log.info("Refresh token 조회: {}", refreshToken.getRefreshToken());

                // 리프레시 토큰 만료 검증
                try {
                    if (jwtUtil.verifyToken(refreshToken.getRefreshToken()) != null) {
                        log.info("️🦄리프레시 토큰 만료 전");
                        String newAccessToken = refreshTokenService.reCreateAccessToken(accessToken, refreshToken);
                        log.info("new 액세스 토큰 발급: {}", newAccessToken);

                        Cookie cookie = new Cookie("Bearer", newAccessToken);
                        cookie.setPath("/");
                        cookie.setSecure(true);
                        cookie.setHttpOnly(true);
                        httpResponse.addCookie(cookie);

                        Claims claims = jwtUtil.extractClaims(newAccessToken);

                        Long userId = Long.valueOf(claims.getSubject());
                        String username = claims.get("username", String.class);
                        UserRole userRole = UserRole.of(claims.get("userRole", String.class));
                        AuthUser authUser = new AuthUser(userId, username, userRole);

                        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (ExpiredJwtException e) {
                    log.info("️🦄리프레시 토큰 만료 됨");
                    log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                } catch (SecurityException | MalformedJwtException e) {
                    log.error("Invalid JWT signature.", e);
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
                    return;
                } catch (UnsupportedJwtException e) {
                    log.error("Unsupported JWT token.", e);
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
                    return;
                } catch (Exception e) {
                    log.error("Internal server error", e);
                    httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }
        }
        chain.doFilter(httpRequest, httpResponse);
    }
}