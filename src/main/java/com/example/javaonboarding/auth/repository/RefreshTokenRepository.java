package com.example.javaonboarding.auth.repository;

import com.example.javaonboarding.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAccessToken(String accessToken);
}