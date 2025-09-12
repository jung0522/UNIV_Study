package com.example.board.Controller;

import com.example.board.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> home(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "홈페이지 접근 성공",
            "data", Map.of(
                "user", Map.of(
                    "email", customUserDetails.getUser().getEmail(),
                    "nickname", customUserDetails.getUser().getNickname(),
                    "profileImage", customUserDetails.getUser().getProfileImage()
                )
            )
        ));
    }
}
