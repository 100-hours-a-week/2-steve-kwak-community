package com.example.demo.post.domain;

import com.example.demo.login.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_like")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User와의 다대일 관계 (각 좋아요는 특정 사용자가 작성)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Post와의 다대일 관계 (각 좋아요는 특정 게시글에 적용)
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
