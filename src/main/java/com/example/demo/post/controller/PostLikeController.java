package com.example.demo.post.controller;

import com.example.demo.login.domain.User;
import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostLikeService;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
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

    @PatchMapping("/{postId}/like")
    @ResponseBody
    public ResponseEntity<PostResponseDto> likePost(@PathVariable Long postId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");  // Map에서 userId 가져오기
        User user = userService.findById(userId);  // userId로 User 조회

        if (user == null) {
            return ResponseEntity.badRequest().body(null);  // 유저가 존재하지 않으면 400 에러 반환
        }

        postLikeService.likePost(postId, user);  // 좋아요 처리

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        PostResponseDto postDto = new PostResponseDto(post); // Post -> PostResponseDto 변환
        postDto.setLikeCount(postLikeService.getLikeCountByPostId(postId)); // 해당 게시글의 좋아요 수 설정
        return ResponseEntity.ok(postDto); // 좋아요가 반영된 게시글 데이터를 JSON 형태로 반환
    }
}
