package com.example.demo.login.domain;

import com.example.demo.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더를 통한 생성만 허용
@Builder(toBuilder = true) // 기존 객체를 기반으로 새로운 객체 생성 가능하도록 설정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(name = "profileimage_url", length = 255)
    private String profileImageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Post> posts;

    @Column(nullable = false)
    private boolean isDeleted = false;  // 회원 탈퇴 여부 (기본값: false)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  // 기본 역할은 "USER"

    //빌더 패턴을 활용한 객체 수정
    public User updatePassword(String newPassword) {
        return this.toBuilder()
                .password(newPassword)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updateNickname(String newNickname) {
        return this.toBuilder()
                .nickname(newNickname)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updateProfileImage(String newProfileImageUrl) {
        return this.toBuilder()
                .profileImageUrl(newProfileImageUrl)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User deactivateUser() {
        return this.toBuilder()
                .isDeleted(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
