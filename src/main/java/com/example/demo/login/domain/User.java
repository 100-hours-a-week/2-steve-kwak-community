package com.example.demo.login.domain;

import com.example.demo.post.domain.Comment;
import com.example.demo.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter // 업데이트 가능하도록 추가
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Post> posts;

    @Column(nullable = false)
    private boolean isDeleted = false;  // 회원 탈퇴 여부 (기본값: false)

    @Enumerated(EnumType.STRING)  // Enum 타입으로 저장
    @Column(nullable = false)
    private Role role = Role.USER;  // 기본 역할은 "USER"로 설정

}
