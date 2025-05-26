package com.example.board.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.example.board.config.RedisConfig;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // Redis
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    // 액세스 토큰: 30분
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 30;

    // 리프레시 토큰: 7일
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;

    // JWT 서명 키 객체
    private Key key;

    @PostConstruct
    public void init() {
        // secretKey를 바이트로 변환하여 HMAC 키 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 액세스 토큰 생성
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email) // 사용자 이메일을 subject로 설정
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY)) // 만료 시간
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

        // Redis에 저장 (key: email, value: refreshToken, expiration 설정)
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                REFRESH_TOKEN_VALIDITY,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // 서명 및 만료 검증 포함
                .getBody()
                .getSubject(); // subject가 이메일
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                return true;
            } catch (ExpiredJwtException e) {
                return false; // 만료된 토큰
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
    }
    public boolean isRefreshTokenValid(String email, String refreshToken) {
        String savedToken = (String) redisTemplate.opsForValue().get(email);
        return savedToken != null && savedToken.equals(refreshToken) && validateToken(refreshToken);
    }
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
