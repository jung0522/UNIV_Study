//package com.example.board.Controller;
//
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.servlet.view.RedirectView;
//import com.example.board.Repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import com.example.board.Service.kakaoOAuth2Service;
//import org.springframework.web.bind.annotation.*;
//import com.example.board.Dto.KakaoUserInfoDto;
//import com.example.board.Entity.*;
//import java.util.Optional;
//
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final UserRepository userRepository;
//    private final kakaoOAuth2Service kakaoOAuth2Service;
//
//
//    @GetMapping("/kakao")
//    public RedirectView redirectToKakao() {
//        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" +
//                kakaoOAuth2Service.getClientId() +
//                "&redirect_uri=" + kakaoOAuth2Service.getRedirectUri();
//        return new RedirectView(kakaoLoginUrl);
//    }
//
//    // 카카오 로그인 성공 후 callback 처리
//    @GetMapping("/kakao/callback")
//    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) {
//        String accessToken = kakaoOAuth2Service.getAccessToken(code);
//        KakaoUserInfoDto kakaoUserInfo = kakaoOAuth2Service.getKakaoUserInfo(accessToken);
//
//        Optional<User> optionalUser = userRepository.findByEmail(kakaoUserInfo.email());
//
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            // 닉네임과 프로필 이미지가 변경되었으면 업데이트
//            if (!user.getNickname().equals(kakaoUserInfo.nickname()) ||
//                    !user.getProfileImage().equals(kakaoUserInfo.profileImage())) {
//                user.setNickname(kakaoUserInfo.nickname());
//                user.setProfileImage(kakaoUserInfo.profileImage());  // 프로필 이미지 업데이트
//                userRepository.save(user);
//            }
//        }
//        else {
//            // 새로운 사용자라면 프로필 이미지도 함께 저장
//            userRepository.save(User.builder()
//                    .email(kakaoUserInfo.email())
//                    .nickname(kakaoUserInfo.nickname())
//                    .profileImage(kakaoUserInfo.profileImage())
//                    .build());
//        }
//        System.out.println(kakaoUserInfo);
//        return ResponseEntity.ok("로그인 성공: " + kakaoUserInfo.nickname() + ", accessToken: " + accessToken);
//
//
//    }
//    // 사용자 정보 반환 (테스트용)
//    @GetMapping("/user")
//    public KakaoUserInfoDto getUserInfo(@RequestHeader("Authorization") String token) {
//        if (!token.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Invalid token format");
//        }
//        String accessToken = token.substring(7);
//        return kakaoOAuth2Service.getKakaoUserInfo(accessToken);
//    }
//}
//
//
//
//
//
//
//
//
//
//
