package com.example.board.Handler;

import com.example.board.config.JwtTokenProvider;
import com.example.board.security.CustomOAuth2User;
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
        // OAuth2 인증 성공 후 principal에서 사용자 정보 추출
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getNickname();
        String picture = oAuth2User.getPicture();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // RESTful API 응답 - JSON 형태로 토큰과 사용자 정보 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
            "{\"success\":true,\"message\":\"로그인 성공\",\"data\":{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"user\":{\"email\":\"%s\",\"name\":\"%s\",\"picture\":\"%s\"}}}", 
            accessToken, refreshToken, email, name, picture
        ));
    }
}