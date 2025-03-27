package com.example.demo.post.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.login.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 특정 게시글 조회 (Optional 반환)
    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 작성 (Post 객체 직접 받음)
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시글 수정 (setter 활용)
    // 게시글 수정
    @Transactional
    public Post updatePost(Long postId, Post post, Long userId) {
        Post existingPost = postRepository.findById(postId).orElse(null);

        if (existingPost == null) {
            return null; // 게시글을 찾을 수 없는 경우
        }
        // 작성자와 현재 사용자가 일치하는지 비교
        if (!existingPost.getAuthor().getId().equals(userId)) {
            return null; // 권한 없음
        }
        // 게시글 수정
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        return postRepository.save(existingPost);
    }


    // 게시글 삭제
    @Transactional
    public boolean deletePost(Long postId, Long userId) {
        Post existingPost = postRepository.findById(postId).orElse(null);

        if (existingPost == null) {
            return false; // 게시글을 찾을 수 없는 경우
        }

        // 게시글 작성자 확인
        if (!existingPost.getAuthor().getId().equals(userId)) {
            return false; // 작성자와 현재 사용자가 일치하지 않으면 권한 없음
        }

        // 게시글 삭제
        postRepository.delete(existingPost);
        return true;
    }
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);  // 조회수 1 증가
        postRepository.save(post);  // 변경된 게시글 저장
    }

}
