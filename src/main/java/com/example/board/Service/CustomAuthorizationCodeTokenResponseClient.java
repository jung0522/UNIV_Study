package com.example.board.Service;

import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
// import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;

/**
 * OAuth2 로그인 과정에서 Access Token 및 Refresh Token 정보를 가로채어 로그에 출력하는 클래스.
 * Spring Security의 기본 토큰 클라이언트(DefaultAuthorizationCodeTokenResponseClient)를 래핑하여 사용.
 */
public class CustomAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    // 기본 구현체를 내부적으로 사용하기 위해 인스턴스를 생성
    private final DefaultAuthorizationCodeTokenResponseClient delegate = new DefaultAuthorizationCodeTokenResponseClient();

    /**
     * OAuth2AccessTokenResponse를 얻는 메서드.
     * 내부적으로 Spring Security의 기본 토큰 요청 로직을 수행한 뒤, 그 결과로부터 Access Token과 Refresh Token을 추출하여 출력한다.
     *
     * param authorizationGrantRequest 인가 코드 기반 토큰 요청 객체
     */
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // 기본 구현체를 통해 실제 토큰 요청 수행
        OAuth2AccessTokenResponse response = delegate.getTokenResponse(authorizationGrantRequest);

        System.out.println("Access Token: " + response.getAccessToken().getTokenValue());

        // 리프레시 토큰이 존재하는 경우 출력, 없으면 없음 출력
        if (response.getRefreshToken() != null) {
            System.out.println("Refresh Token: " + response.getRefreshToken().getTokenValue());
        } else {
            System.out.println("Refresh Token: 없음");
        }

        // 결과 반환
        return response;
    }
}
