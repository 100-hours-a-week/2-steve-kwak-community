package com.example.demo.post.domain;

import com.example.demo.login.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자 보호
@AllArgsConstructor
@Builder(toBuilder = true)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference  // 역참조
    private User author;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private int commentCount = 0;  // 기본값 설정

    @Column(nullable = false)
    @Builder.Default
    private int viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 순환 참조 방지
    private List<Comment> comments;

    // 댓글 수 증가
    public Post increaseCommentCount() {
        return this.toBuilder()
                .commentCount(this.commentCount + 1)
                .build();
    }

    // 조회수 증가
    public Post increaseViewCount() {
        return this.toBuilder()
                .viewCount(this.viewCount + 1)
                .build();
    }

    // 좋아요 수 증가
    public Post increaseLikeCount() {
        return this.toBuilder()
                .likeCount(this.likeCount + 1)
                .build();
    }
    // 빌더 패턴을 활용하여 객체 수정
    public Post updatePost(String title, String content) {
        return this.toBuilder()
                .title(title)
                .content(content)
                .updatedAt(LocalDateTime.now())  // 수정 시 수정 시간 갱신
                .build();
    }
}
