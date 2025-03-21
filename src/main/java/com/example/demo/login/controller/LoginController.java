package com.example.demo.login.controller;

import com.example.demo.login.domain.User;
import com.example.demo.login.service.LoginService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;  // JwtUtil 주입

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // 사용자가 존재하는지, 그리고 인증이 완료되었는지 확인
        User authenticatedUser = loginService.authenticateUser(email, password);

        if (authenticatedUser == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 또는 비밀번호가 잘못되었습니다."));
        }

        // JWT 생성 - userId를 사용
        String token = jwtUtil.generateToken(authenticatedUser.getId());  // userId로 JWT 생성

        return ResponseEntity.ok(Map.of(
                "token", token  // 응답에 JWT 포함
        ));
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
