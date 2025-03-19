package com.example.demo.post.controller;

import com.example.demo.post.domain.Comment;
import com.example.demo.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회 (닉네임 포함)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable Long postId) {
        List<Map<String, Object>> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }


    @PostMapping
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> requestData) {  // JSON 요청 처리
        Long userId = Long.parseLong(requestData.get("userId")); // userId 추출
        String content = requestData.get("content");

        Comment createdComment = commentService.createComment(postId, userId, content);
        return ResponseEntity.ok(createdComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
