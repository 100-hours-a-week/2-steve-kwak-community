package com.example.demo.login.service;

import com.example.demo.login.domain.User;
import com.example.demo.login.repository.UserRepository;
import com.example.demo.post.repository.CommentRepository;
import com.example.demo.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    // 사용자 ID로 사용자 조회
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);  // Optional<User> 반환
        return user.orElse(null);  // 사용자 없으면 null 반환
    }
    // 닉네임 변경 (예외 던지는 방식)
    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User updatedUser = user.updateNickname(newNickname);
        userRepository.save(updatedUser);
    }


    // 비밀번호 변경 (예외 던지는 방식)
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User updatedUser = user.updatePassword(newPassword);
        userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        postLikeRepository.deleteByUserId(userId);
        commentRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }
}
