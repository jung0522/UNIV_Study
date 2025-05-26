package com.example.board.Handler;

import com.example.board.config.JwtTokenProvider;
import com.example.board.security.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

//     원래   String email = authentication.getName();
        // OAuth2 인증 성공 후 principal에서 사용자 정보 추출
//        일반 로그인: UserDetails 객체
//        OAuth2 로그인: OAuth2User 또는 우리가 만든 CustomOAuth2User
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = authentication.getName(); // 또는 oAuth2User.getEmail()

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(email);
//        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // 구글 로그인 시 쿠키에 jwt 토큰 안 보여서 로그로 출력
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " +refreshToken);

        // 쿠키로 AccessToken 저장 (HttpOnly로 클라이언트 JS 접근 불가)
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 60); // 30분

        // 리프레시 토큰 쿠키 생성
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

        // 응답에 쿠키 추가
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 로그인 후 /home으로 리다이렉트
        response.sendRedirect("/home");
    }
}
