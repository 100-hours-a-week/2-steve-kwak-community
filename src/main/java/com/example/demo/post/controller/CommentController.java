package com.example.demo.post.controller;

import com.example.demo.post.domain.Comment;
import com.example.demo.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


    @PostMapping()
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> requestData) {
        try {
            if (!requestData.containsKey("userId") || !requestData.containsKey("content")) {
                return ResponseEntity.badRequest().body("userId 또는 content가 없습니다.");
            }

            Long userId = Long.parseLong(requestData.get("userId")); // userId 변환
            String content = requestData.get("content");

            Comment createdComment = commentService.createComment(postId, userId, content);
            return ResponseEntity.ok(createdComment);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("userId는 숫자여야 합니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류 발생");
        }
    }


    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
