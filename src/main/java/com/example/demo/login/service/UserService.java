package com.example.demo.login.service;

import com.example.demo.login.domain.User;
import com.example.demo.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 사용자 ID로 사용자 조회
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);  // Optional<User> 반환
        return user.orElse(null);  // 사용자 없으면 null 반환
    }
}
