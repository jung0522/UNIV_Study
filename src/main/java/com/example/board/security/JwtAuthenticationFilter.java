package com.example.board.security;

import com.example.board.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청이 들어올 때마다 JWT 토큰을 확인해서 인증 정보를 SecurityContext에 설정하는 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 JWT 추출
        String token = resolveToken(request);

        // 토큰이 존재하고 유효하면 인증 정보 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);

            // UserDetailsService 사용
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // 인증 객체 생성 및 SecurityContext에 저장
            // 인증 객체 생성: 사용자 정보를 기반으로 UsernamePasswordAuthenticationToken 객체 생성
            // 첫 번째 인자: 인증된 사용자 정보 (UserDetails)
            // 두 번째 인자: 자격 증명 (OAuth2의 경우 비밀번호가 없으므로 null)
            // 세 번째 인자: 사용자 권한 목록
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 요청 정보를 인증 객체에 추가 (IP, 세션 ID 등 부가 정보 저장)
            // WebAuthenticationDetailsSource는 현재 요청(request)로부터 상세 정보를 추출해 설정
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // 생성한 인증 객체를 SecurityContext에 저장하여 인증 상태로 만듦
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더 또는 쿠키에서 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 쿠키에서 accessToken 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
