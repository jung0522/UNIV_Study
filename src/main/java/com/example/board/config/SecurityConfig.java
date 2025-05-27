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
                .csrf().disable()  // CSRF 보호 비활성화
                // 스프링 시큐리티의 기본 로그아웃은 세션 기반이면 사용 충분
                // 세션 무효화 (invalidateHttpSession(true))
                // SecurityContext 정리
                // logout URL 접근 시 로그아웃 처리
                // 로그아웃 후 지정된 URL로 리디렉션 (logoutSuccessUrl())
                .logout().disable() // ✅ 이 줄을 꼭 추가해야 커스텀 logout API가 작동함
//        CSRF는 공격자가 사용자의 권한을 도용해 악의적인 요청을 보내는 공격 방식입니다
//        Spring Security는 기본적으로 CSRF 보호를 활성화합니다.
//        세션 기반이 아닌 REST API 서버에서, JWT를 사용하는 경우에도 일반적으로 CSRF를 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 정적 리소스는 인증 없이 접근 허용
                        .requestMatchers("/", "/login**", "/token/refresh", "/logout",
                                "/post/**",  "/css/**", "/js/**").permitAll()
                        // 그 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login") // 커스텀 로그인 페이지 설정
                                /* 구글 리프레쉬 토큰   .authorizationEndpoint(config -> config
                                            .authorizationRequestResolver(customAuthorizationRequestResolver())
                                    ) */
                                .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 JWT 생성 및 처리
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)) // 사용자 정보 가져오는 서비스 등록
                 /*  엑세스 토큰, 리프레쉬 토큰     .tokenEndpoint(token -> token
                                .accessTokenResponseClient(authorizationCodeTokenResponseClient())
                        ) */
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
