package com.example.demo.login.service;

import com.example.demo.login.domain.User;
import com.example.demo.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(SignupService.class);


    @Transactional
    public User registerUser(User user) {
        // 중복 이메일 검사
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("회원가입 오류: ", e);
            throw new RuntimeException("회원가입 중 오류가 발생했습니다.");
        }
    }
}
