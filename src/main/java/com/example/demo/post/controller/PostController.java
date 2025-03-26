package com.example.demo.post.controller;

import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import com.example.demo.login.domain.User;
import com.example.demo.post.service.StorageService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 게시글 목록 조회
    @GetMapping
    public String getAllPostsPage() {
        return "posts";
    }

    @GetMapping("/makepost")
    public String getMakePostPage() {
        return "makepost";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<PostResponseDto>> getAllPostsApi() {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PostResponseDto> postDto = postService.getAllPosts().stream()
                .map(PostResponseDto::new)
                .toList();

        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/api1/{postId}")
    public String getPostDetailPage(@PathVariable Long postId, Model model) {
        Post post = getPostOrThrow(postId);
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
    public ResponseEntity<?> getPostDetailApi(@PathVariable Long postId) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Post post = getPostOrThrow(postId);
        postService.incrementViewCount(postId);
        return ResponseEntity.ok(new PostResponseDto(post));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> request) {
        User author = userService.findById(Long.parseLong(request.get("userId")));
        if (author == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Post post = Post.builder()
                .author(author)
                .title(request.get("title"))
                .content(request.get("content"))
                .imageUrl(request.getOrDefault("imageUrl", null))
                .commentCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(postService.createPost(post));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> postData, HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Post post = new Post();
        post.setTitle(postData.get("title"));
        post.setContent(postData.get("content"));

        Post updatedPost = postService.updatePost(postId, post, userId);
        if (updatedPost == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (postService.deletePost(postId, userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = storageService.saveFile(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "이미지 업로드 실패"));
        }
    }

    // ===== 중복 코드 최적화를 위한 공통 메서드 =====

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        return null;
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return jwtUtil.extractUserId(token.substring(7));
    }

    private Post getPostOrThrow(Long postId) {
        return postService.getPostById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }
}
