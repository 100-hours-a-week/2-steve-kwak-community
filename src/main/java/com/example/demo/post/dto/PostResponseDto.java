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
    private String author;  // author는 이제 User의 nickname을 포함
    private int commentCount;
    private int viewCount;
    private int likeCount;
    private String createdAt;  // createdAt 필드 추가

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        this.author = post.getAuthor().getNickname();  // author의 nickname을 사용
        this.likeCount = post.getLikeCount();  // likeCount를 추가해야 합니다.
        this.createdAt = post.getCreatedAt().toString();  // createdAt 필드 추가 및 변환
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
