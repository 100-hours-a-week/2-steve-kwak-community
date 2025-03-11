package com.example.demo.login.controller;

import com.example.demo.login.domain.User;
import com.example.demo.login.service.LoginService;
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        User authenticatedUser = loginService.authenticateUser(email, password);

        if (authenticatedUser == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 또는 비밀번호가 잘못되었습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "id", authenticatedUser.getId(),
                "nickname", authenticatedUser.getNickname()
        ));
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
