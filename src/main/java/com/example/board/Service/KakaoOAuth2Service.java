//package com.example.board.Service;
//
//import com.example.board.Dto.KakaoUserInfoDto;
//import com.fasterxml.jackson.databind.JsonNode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoOAuth2Service {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${kakao.client.id}")
//    private String clientId;
//
//    @Value("${kakao.redirect.uri}")
//    private String redirectUri;
//
//    @Value("${kakao.token.url}")
//    private String tokenUrl;
//
//    @Value("${kakao.api.url}")
//    private String apiUrl;
//
//    public String getClientId() {
//        return clientId;
//    }
//
//    public String getRedirectUri() {
//        return redirectUri;
//    }
//    public String getTokenUrl() {
//        return tokenUrl;
//    }
//    public String getApiUrl() {
//        return apiUrl;
//    }
//
//
//
//
//    public String getAccessToken(String authorizationCode) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        params.add("redirect_uri", redirectUri);
//        params.add("code", authorizationCode);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        try {
//            ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, request, JsonNode.class);
//            System.out.println("카카오 응답: " + response.getBody()); // 디버깅용
//
//            if (response.getBody() != null && response.getBody().has("access_token")) {
//                return response.getBody().get("access_token").asText();
//            }
//        } catch (HttpClientErrorException e) {
//            System.err.println("카카오 로그인 오류: " + e.getResponseBodyAsString()); // 디버깅용
//            throw new RuntimeException("카카오 로그인 중 오류 발생: " + e.getMessage());
//        }
//
//        throw new RuntimeException("액세스 토큰을 받아올 수 없습니다.");
//    }
//    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> request = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, JsonNode.class);
//            if (response.getBody() != null) {
//                JsonNode userInfo = response.getBody();
//                JsonNode kakaoAccount = userInfo.path("kakao_account");
//                JsonNode profile = kakaoAccount.path("profile");
//
//                String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : "no-email";
//                String nickname = profile.has("nickname") ? profile.get("nickname").asText() : "사용자";
//                String profileImage = profile.has("profile_image_url") ? profile.get("profile_image_url").asText() : null;
//
//                return new KakaoUserInfoDto(email, nickname, profileImage);
//            }
//        } catch (HttpClientErrorException e) {
//            throw new RuntimeException("카카오 사용자 정보를 가져오는 중 오류 발생: " + e.getMessage());
//        }
//
//        throw new RuntimeException("사용자 정보를 받아올 수 없습니다.");
//    }
//}
//
//
//
//
