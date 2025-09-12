package com.example.board.Controller;

import com.example.board.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // Authorization 헤더에서 refreshToken 추출
        String authHeader = request.getHeader("Authorization");
        String refreshToken = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // Redis에서 refreshToken 비교
            if (jwtTokenProvider.isRefreshTokenValid(email, refreshToken)) {
                // 1. 기존 refreshToken을 Blacklist에 추가 (무효화)
                jwtTokenProvider.addTokenToBlacklist(refreshToken);
                
                // 2. Redis에서 기존 refreshToken 삭제
                jwtTokenProvider.deleteRefreshToken(email);
                
                // 3. 새로운 토큰들 생성
                String newAccessToken = jwtTokenProvider.createAccessToken(email);
                String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
                
                // 4. 새로운 refreshToken을 Redis에 저장
                jwtTokenProvider.saveRefreshToken(email, newRefreshToken);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "토큰 갱신 성공",
                    "data", Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", newRefreshToken
                    )
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "success", false,
            "message", "유효하지 않거나 만료된 리프레시 토큰"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 토큰이 없거나 유효하지 않은 경우 에러 응답
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "인증 토큰이 필요합니다."
            ));
        }

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "유효하지 않은 토큰입니다."
            ));
        }

        // 토큰이 유효한 경우에만 로그아웃 처리
        String email = jwtTokenProvider.getEmailFromToken(token);
        
        // 1. Access Token을 Blacklist에 추가 (무효화)
        jwtTokenProvider.addTokenToBlacklist(token);
        
        // 2. Redis에서 Refresh Token 삭제
        jwtTokenProvider.deleteRefreshToken(email);

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그아웃 되었습니다."
        ));
    }


}

