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

    // 비밀번호 변경
    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("password");
            userService.updatePassword(userId, newPassword);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 비밀번호 변경 페이지 이동
    @GetMapping("/{userId}/password")
    public String editPasswordPage(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "editpassword";
    }

    // 프로필 수정 페이지 이동
    @GetMapping("/{userId}/profile")
    public String editProfilePage(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "editprofile";
    }

    // 닉네임 변경
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateNickname(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newNickname = request.get("nickname");
            userService.updateNickname(userId, newNickname);
            return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
