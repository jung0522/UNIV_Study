package com.example.board.Controller;

import com.example.board.Repository.UserRepository;
import com.example.board.config.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // 쿠키에서 refreshToken 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // Redis에서 refreshToken 비교
            if (jwtTokenProvider.isRefreshTokenValid(email, refreshToken)) {
                String newAccessToken = jwtTokenProvider.createAccessToken(email);

                // 새 AccessToken 쿠키로 전달
                Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
                newAccessCookie.setHttpOnly(true);
                newAccessCookie.setPath("/");
                newAccessCookie.setMaxAge(30 * 60);
                response.addCookie(newAccessCookie);

                return ResponseEntity.ok("Access token refreshed");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            // Redis에서 리프레시 토큰 삭제
            redisTemplate.delete(email);
        }

        // 클라이언트 쿠키에서 토큰 삭제 (만료시간 0으로 설정)
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


}

