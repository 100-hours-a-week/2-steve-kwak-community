package com.example.demo.login.controller;

import com.example.demo.login.domain.Role;
import com.example.demo.login.domain.User;
import com.example.demo.login.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    // 회원가입 페이지로 이동 (GET 요청)
    @GetMapping("/users")
    public String showSignupPage() {
        return "signup";  // resources/templates/signup.html 파일을 렌더링
    }

    // 회원가입 처리 (POST 요청)
    @PostMapping("/users")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");
        String nickname = userData.get("nickname");
        String profileImageUrl = userData.get("profileImageUrl");
        LocalDateTime now = LocalDateTime.now();

        User newUser = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .createdAt(now)
                .updatedAt(now)
                .role(Role.USER)
                .build();

        User savedUser = signupService.registerUser(newUser);
        return ResponseEntity.ok(savedUser);
    }
}
