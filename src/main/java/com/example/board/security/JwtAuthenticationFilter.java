package com.example.board.security;

import com.example.board.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

            // UserDetailsService 사용 - CustomUserDetails로 캐스팅
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            // 인증 객체 생성 및 SecurityContext에 저장
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 요청 정보를 인증 객체에 추가
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 생성한 인증 객체를 SecurityContext에 저장하여 인증 상태로 만듦
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 JWT 토큰 추출 (RESTful API 방식)
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
