package com.example.demo.post.controller;

import com.example.demo.post.domain.Comment;
import com.example.demo.post.service.CommentService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable Long postId) {
        List<Map<String, Object>> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 작성 (JWT 인증 추가)
    @PostMapping
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> requestData,
            HttpServletRequest request) {
        try {
            jwtUtil.validateTokenFromRequest(request);  // 토큰 검증
            Long userId = jwtUtil.extractUserIdFromRequest(request);

            if (!requestData.containsKey("content")) {
                return ResponseEntity.badRequest().body("content가 없습니다.");
            }
            String content = requestData.get("content");

            Comment createdComment = commentService.createComment(postId, userId, content);
            return ResponseEntity.ok(createdComment);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류 발생");
        }
    }

    // 댓글 삭제 (JWT 인증 추가)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            jwtUtil.validateTokenFromRequest(request); // 토큰 검증
            Long userId = jwtUtil.extractUserIdFromRequest(request);

            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생");
        }
    }
}
