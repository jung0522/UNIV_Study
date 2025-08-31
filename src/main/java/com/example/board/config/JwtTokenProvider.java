package com.example.board.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // 액세스 토큰: 30분
    private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30;

    // 리프레시 토큰: 7일
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;

    @PostConstruct
    public void init() {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 액세스 토큰 생성
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String redisKey = getRedisKey(email);
        redisTemplate.opsForValue().set(redisKey, refreshToken, REFRESH_TOKEN_VALIDITY, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    // Redis key 구조 (운영 환경에서는 email:deviceId 등으로 확장 가능)
    private String getRedisKey(String email) {
//        "RT:" → Refresh Token임을 나타내는 접두사(prefix)
        return "RT:" + email;
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증 (서명)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 만료
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 변조 등
        }
    }

    // 토큰 만료 여부 체크
    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // 리프레시 토큰 검증
    public boolean isRefreshTokenValid(String email, String refreshToken) {
        String redisKey = getRedisKey(email);
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            return false;
        }

        if (isTokenExpired(refreshToken)) {
            deleteRefreshToken(email); // 만료된 토큰 자동 삭제
            return false;
        }
        return true;
    }

    public void deleteRefreshToken(String email) {
        String redisKey = getRedisKey(email);
        redisTemplate.delete(redisKey);
    }
}
