package com.example.demo.post.dto;

import com.example.demo.post.domain.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private int commentCount;
    private int viewCount;
    private int likeCount;
    private String createdAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        this.author = post.getAuthor().getNickname();
        this.createdAt = post.getCreatedAt().toString();

        // 🔥 회원 탈퇴한 사용자의 좋아요 제외
        this.likeCount = (int) post.getLikes().stream()
                .filter(like -> !like.getUser().isDeleted()) // 탈퇴하지 않은 사용자만 카운트
                .count();
    }
}
