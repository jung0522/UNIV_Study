package com.example.board.config;

import com.example.board.Handler.OAuth2SuccessHandler;
import com.example.board.Service.CustomOAuth2UserService;
//import com.example.board.Service.CustomAuthorizationCodeTokenResponseClient;
import com.example.board.security.CustomUserDetailsService;
import com.example.board.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity // Spring Security 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService; // 사용자 정보 처리 서비스
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // OAuth2 로그인 성공 핸들러
    // private final ClientRegistrationRepository clientRegistrationRepository;
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성 및 검증 클래스
    private final CustomUserDetailsService customUserDetailsService; // 사용자 인증 처리 클래스



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**") // API 엔드포인트는 CSRF 제외
                )
                .logout().disable() // 커스텀 logout API 사용
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스와 OAuth2 관련 경로는 인증 없이 접근 허용
                        .requestMatchers("/", "/oauth2/**", "/css/**", "/js/**").permitAll()
                        // API 인증 관련 경로는 인증 없이 접근 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        // 게시글 조회는 인증 없이 접근 허용
                        .requestMatchers("GET", "/api/posts/**").permitAll()
                        // 게시글 작성, 수정, 삭제는 인증 필요
                        .requestMatchers("POST", "/api/posts/**").authenticated()
                        .requestMatchers("PUT", "/api/posts/**").authenticated()
                        .requestMatchers("DELETE", "/api/posts/**").authenticated()
                        // 홈페이지와 사용자 정보는 인증 필요
                        .requestMatchers("/api/home").authenticated()
                        .requestMatchers("/api/user/**").authenticated()
                        // 그 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/api/auth/login") // RESTful API 로그인 페이지
                                .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 JWT 생성 및 처리
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)) // 사용자 정보 가져오는 서비스 등록
                );



        // JWT 인증 필터 등록 - UsernamePasswordAuthenticationFilter 전에 위치
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
/*
     // OAuth2 요청 URL 커스터마이징 (예: access_type=offline 추가 등)
    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        return new CustomAuthorizationRequestResolver(clientRegistrationRepository);
    }

    // 엑세스/리프레쉬 토큰 처리 커스터마이징
    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
        return new CustomAuthorizationCodeTokenResponseClient();
    }

 */
}