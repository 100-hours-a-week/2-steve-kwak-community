package com.example.demo.post.repository;

import com.example.demo.post.domain.PostLike;
import com.example.demo.post.domain.Post;
import com.example.demo.login.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 게시글의 좋아요 수를 계산하는 메서드
    int countByPostId(Long postId);  // 해당 게시글에 달린 좋아요 수 반환

    // 특정 게시글에 대한 특정 사용자의 좋아요 존재 여부 확인
    boolean existsByPostAndUser(Post post, User user);
}
