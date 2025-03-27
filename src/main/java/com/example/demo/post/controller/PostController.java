package com.example.demo.post.controller;

import com.example.demo.login.domain.User;
import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import com.example.demo.post.service.StorageService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final StorageService storageService;

    // 게시글 목록 조회 (페이지 반환)
    @GetMapping
    public String getAllPostsPage() {
        return "posts";
    }

    @GetMapping("/makepost")
    public String getMakePostPage() {
        return "makepost";
    }

    // 게시글 목록 조회 (JSON API)
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<PostResponseDto>> getAllPostsApi() {
        List<PostResponseDto> postDto = postService.getAllPosts().stream()
                .map(PostResponseDto::new)
                .toList();

        return ResponseEntity.ok(postDto);
    }

    // 게시글 상세 페이지 반환
    @GetMapping("/api1/{postId}")
    public String getPostDetailPage(@PathVariable Long postId, Model model) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        model.addAttribute("postId", postId);
        model.addAttribute("post", post);
        model.addAttribute("authorUserId", post.getAuthor().getId());

        return "postdetail";
    }

    @GetMapping("/postedit/{postId}")
    public String getEditDetailPage(@PathVariable Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "postedit";
    }

    // 게시글 상세 조회 (JSON API)
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> getPostDetailApi(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        postService.incrementViewCount(postId);

        return ResponseEntity.ok(new PostResponseDto(post));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User author = userService.findById(userId);
        if (author == null) {
            return ResponseEntity.badRequest().body(null);
        }

        String imageUrl = request.getOrDefault("imageUrl", null);
        Post post = Post.builder()
                .author(author)
                .title(request.get("title"))
                .content(request.get("content"))
                .imageUrl(imageUrl)
                .commentCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(postService.createPost(post));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post post, HttpServletRequest request) {
        // 현재 로그인된 사용자 ID 추출
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 게시글 수정 서비스 호출
        Post updatedPost = postService.updatePost(postId, post, userId);

        // 수정된 게시글이 없으면 권한 오류
        if (updatedPost == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 수정된 게시글 반환
        return ResponseEntity.ok(updatedPost);
    }



    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (postService.deletePost(postId, userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 이미지 업로드
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = storageService.saveFile(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "이미지 업로드 실패"));
        }
    }
    // JWT에서 userId 추출하는 메서드 (공통화)
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return jwtUtil.extractUserId(token.substring(7)); // "Bearer " 제거 후 추출
    }
}
