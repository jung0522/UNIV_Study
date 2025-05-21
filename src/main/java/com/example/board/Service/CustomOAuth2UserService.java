

package com.example.board.Service;

import com.example.board.Entity.User;
import com.example.board.Repository.UserRepository;
import com.example.board.config.JwtTokenProvider;
import com.example.board.security.CustomOAuth2User;
import com.example.board.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // AccessToken 출력
//        String accessToken = userRequest.getAccessToken().getTokenValue();
//        System.out.println("Access Token: " + accessToken);

        // OAuth2 로그인 플랫폼 구분: google, naver, kakao 등
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
        // 기본 OAuth2UserService를 통해 사용자 정보 조회
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);



        Map<String, Object> attributes = oAuth2User.getAttributes();

//        소셜 로그인에서 userNameAttributeName은 OAuth2 인증 응답 중
//        사용자를 식별하기 위한 "고유 ID 속성"의 이름을 지정하는 값입니다.
        String userNameAttributeName;

        String email;
        String nickname;
        String profileImage;

        System.out.println(attributes);
        System.out.println(registrationId);

        //  Kakao 응답 처리
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//            email = (String) kakaoAccount.get("email");
            // 이메일 하드코딩
            email = "jjy1111202@kakao.com";
            nickname = (String) profile.get("nickname");
            profileImage = (String) profile.get("profile_image_url");

            userNameAttributeName = "id";

            //  Google 응답 처리
        } else if ("google".equals(registrationId)) {
            Object idToken = userRequest.getAdditionalParameters().get("id_token");
            System.out.println("ID Token: " + idToken);

            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
            profileImage = (String) attributes.get("picture");
            userNameAttributeName = "sub";

            //  Naver 응답 처리
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            profileImage = (String) response.get("profile_image");
            userNameAttributeName = "id";
            // 실제 사용할 attributes를 Naver의 response로 변경
            // reponse 안에 토큰이 답겨있음
            attributes = response;


        } else {
            email = null;
            nickname = null;
            profileImage = null;
            userNameAttributeName = null;

        }

        // DB에 사용자 정보 저장 또는 수정
        User user = userRepository.findByEmail(email)
                .map(entity -> {
                    entity.setNickname(nickname);
                    entity.setProfileImage(profileImage);
                    return userRepository.save(entity);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .nickname(nickname)
                                .profileImage(profileImage)
                                .build()
                ));




//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                attributes,
//                userNameAttributeName
//        );

        // 반환할 사용자 객체 생성 (커스텀 OAuth2 사용자)
        return new CustomOAuth2User(
                attributes,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                email
        );



    }
}

