package com.example.demo.post.controller;

import com.example.demo.login.domain.User;
import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostLikeService;
import com.example.demo.post.service.PostService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostService postService;
    private final UserService userService;
    private final PostLikeService postLikeService;
    private final JwtUtil jwtUtil;

    @PatchMapping("/{postId}/like")
    @ResponseBody
    public ResponseEntity<PostResponseDto> likePost(@PathVariable Long postId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 토큰이 없거나 형식이 잘못됨
        }

        token = token.substring(7);  // "Bearer "를 제거하고 토큰만 추출

        // JWT에서 사용자 ID 추출
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 유효한 userId가 아닐 경우
        }

        // userId로 User 조회
        User user = userService.findById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(null);  // 유저가 존재하지 않으면 400 에러 반환
        }

        // 좋아요 처리
        postLikeService.likePost(postId, user);

        // 게시글 조회
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // PostResponseDto 변환
        PostResponseDto postDto = new PostResponseDto(post);
        postDto.setLikeCount(postLikeService.getLikeCountByPostId(postId));  // 좋아요 수 설정

        // 응답
        return ResponseEntity.ok(postDto);  // 좋아요가 반영된 게시글 데이터를 JSON 형태로 반환
    }

}
