package com.example.board.Controller;

import com.example.board.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "인증되지 않은 사용자입니다."
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "현재 사용자 정보",
            "data", Map.of(
                "email", customUserDetails.getUser().getEmail(),
                "nickname", customUserDetails.getUser().getNickname(),
                "profileImage", customUserDetails.getUser().getProfileImage()
            )
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLoginStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isLoggedIn", false,
                "message", "로그아웃 상태입니다."
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "isLoggedIn", true,
            "message", "로그인 상태입니다.",
            "email", customUserDetails.getUser().getEmail()
        ));
    }
}

