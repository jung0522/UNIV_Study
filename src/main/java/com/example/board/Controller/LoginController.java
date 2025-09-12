package com.example.board.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> loginPage() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그인 페이지 접근",
            "data", Map.of(
                "kakaoLoginUrl", "/oauth2/authorization/kakao",
                "naverLoginUrl", "/oauth2/authorization/naver", 
                "googleLoginUrl", "/oauth2/authorization/google"
            )
        ));
    }
}

