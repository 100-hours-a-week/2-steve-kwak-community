package com.example.demo.login.controller;

import com.example.demo.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        boolean updated = userService.updatePassword(userId, newPassword);

        if (updated) {
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호 변경에 실패했습니다.");
        }
    }
    @GetMapping("/{userId}/password")
    public String editPasswordPage(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "editpassword";
    }


}