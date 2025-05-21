/* 구글 리프레쉬 토큰 얻기 위해서
package com.example.board.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.HashMap;
import java.util.Map;

//
 // Google OAuth 요청 시, 권한 동의 창을 항상 뜨게 하고 Refresh Token을 발급받기 위해
//  access_type=offline, prompt=consent 파라미터를 요청 URL에 추가하는 Resolver 클래스.
 //

//OAuth2AuthorizationRequestResolver는 Spring Security에서 OAuth2 로그인 요청 URL을 생성하고,
// 추가 파라미터를 구성할 수 있도록 도와주는 인터페이스입니다.
// 이를 통해 로그인 요청을 세밀하게 커스터마이징할 수 있습니다
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        // Spring Security 기본 리졸버 사용
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest requestFromDefault = defaultResolver.resolve(request);
        return customizeRequest(requestFromDefault);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest requestFromDefault = defaultResolver.resolve(request, clientRegistrationId);
        return customizeRequest(requestFromDefault);
    }

    //
     // 추가 파라미터(access_type=offline, prompt=consent)를 삽입하는 메서드
     //
    private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest request) {
        if (request == null) return null;

        // 기존 파라미터 복사 후 새로운 파라미터 추가
        Map<String, Object> additionalParams = new HashMap<>(request.getAdditionalParameters());
        additionalParams.put("access_type", "offline"); // refresh token 발급 위해 필요
        additionalParams.put("prompt", "consent");     // 동의 창 항상 표시

        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParams)
                .build();
    }
}
*/
