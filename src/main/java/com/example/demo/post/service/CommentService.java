package com.example.demo.post.service;

import com.example.demo.login.domain.User;
import com.example.demo.login.repository.UserRepository;
import com.example.demo.post.domain.Comment;
import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.CommentRepository;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회 (Map으로 변환)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<Map<String, Object>> commentList = new ArrayList<>();

        for (Comment comment : comments) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("content", comment.getContent());
            commentMap.put("createdAt", comment.getCreatedAt());
            commentMap.put("nickname", comment.getUser().getNickname());  // 사용자 닉네임
            commentList.add(commentMap);
        }
        return commentList;
    }


    // 댓글 작성
    @Transactional
    public Comment createComment(Long postId, Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID입니다."));

        // 댓글 객체 생성
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 댓글 저장
        commentRepository.save(comment);

        // 게시글의 댓글 수 증가 후 저장
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return comment;
    }


    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        commentRepository.delete(comment);
    }

}
