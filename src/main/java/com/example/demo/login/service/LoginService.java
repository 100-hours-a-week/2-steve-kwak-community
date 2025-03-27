package com.example.demo.login.service;

import com.example.demo.login.domain.User;
import com.example.demo.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    @Transactional(readOnly = true)
    public User authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return user; // 인증 성공 시 User 객체 반환
            }
        }

        return null; // 인증 실패 시 null 반환
    }
}
