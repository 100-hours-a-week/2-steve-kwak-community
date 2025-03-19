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
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 특정 게시글 조회 (Optional 반환)
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 작성 (Post 객체 직접 받음)
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시글 수정 (setter 활용)
    @Transactional
    public Optional<Post> updatePost(Long postId, String title, String content, String imageUrl) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(title);
            post.setContent(content);
            post.setImageUrl(imageUrl);
            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        });
    }


    // 게시글 삭제 (boolean 반환)
    @Transactional
    public boolean deletePost(Long postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);  // 조회수 1 증가
        postRepository.save(post);  // 변경된 게시글 저장
    }

}
