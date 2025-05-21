package com.example.board.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth2 인증 후 반환되는 사용자 정보를 담기 위한 클래스.
 * OAuth2User를 구현하여 Spring Security에서 사용자 정보를 처리할 수 있도록 함.
 */
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    // OAuth2 제공자로부터 전달받은 사용자 정보(Map 형식)
    private final Map<String, Object> attributes;

    // 사용자의 권한 정보 (예: ROLE_USER)
    private final Collection<? extends GrantedAuthority> authorities;

    // 사용자 이메일 (Spring Security에서 사용자 식별자로 사용)
    private final String email;

    /**
     * 사용자 속성 반환 (이름, 이메일, 프로필 이미지 등)
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 사용자 권한 반환 (Spring Security에서 권한 체크에 사용)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자의 고유 식별자 반환 (Spring Security의 Authentication.getName()에서 사용됨)
     * 여기서는 email을 사용자 식별자로 사용함.
     */
    @Override
    public String getName() {
        return email; // ★ 여기서 이메일을 사용자 이름(식별자)으로 반환
    }
}
