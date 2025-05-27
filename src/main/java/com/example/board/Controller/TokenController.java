package com.example.board.Controller;

import com.example.board.Repository.UserRepository;
import com.example.board.config.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

//    하지만 JWT 기반이라면?
//    현재 구조로 JWT 인증을 사용하고 있다:
//    로그인 성공 시 JWT access token & refresh token을 쿠키에 저장
//    refresh token은 Redis에 저장됨
//    인증은 세션이 아닌 JWT 토큰으로 처리됨 (JwtAuthenticationFilter 사용)

//    이 구조에서 기본 스프링 시큐리티 로그아웃만 쓰면 다음 문제가 발생한다:

//  1. 쿠키 자동 삭제 안 됨: Spring Security 기본 로그아웃은 access/refresh 토큰 쿠키를 삭제해주지 않음
//  2. Redis에서 refreshToken 삭제 안 됨: 직접 Redis에서 해당 사용자 refreshToken을 제거해야 함
//  3. JWT 자체는 무효화 불가: JWT는 상태 저장이 아니므로 서버가 무효화할 수 없고, 로그아웃 시 쿠키를 지워야 의미 있음
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

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


}

