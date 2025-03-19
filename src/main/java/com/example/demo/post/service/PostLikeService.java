package com.example.demo.post.service;

import com.example.demo.login.domain.User;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostLike;
import com.example.demo.post.repository.PostLikeRepository;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    // 특정 게시글의 좋아요 수 계산
    public int getLikeCountByPostId(Long postId) {
        return postLikeRepository.countByPostId(postId); // 해당 postId에 대한 좋아요 수 반환
    }

    // 게시글 좋아요 처리
    @Transactional
    public void likePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 이미 좋아요한 게시글인지 확인
        boolean alreadyLiked = postLikeRepository.existsByPostAndUser(post, user);
        if (alreadyLiked) {
            throw new IllegalArgumentException("이미 좋아요한 게시글입니다.");
        }

        // 새로운 좋아요 생성
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
        // 게시글의 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);  // likeCount 증가
        postRepository.save(post);  // 변경된 게시글 저장
    }

}
