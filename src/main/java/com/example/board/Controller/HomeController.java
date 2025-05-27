package com.example.board.Controller;

import com.example.board.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        model.addAttribute("user", customUserDetails.getUser());
        return "home";
    }

}
