package com.example.demo.post.controller;

import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostLikeService;
import com.example.demo.post.service.PostService;
import com.example.demo.login.domain.User;
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

    // 게시글 목록 조회
    @GetMapping
    public String getAllPostsPage() {
        return "posts"; // templates 폴더에서 posts.html 반환
    }
    @GetMapping("/makepost")
    public  String getMakePostPage(){
        return "makepost";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<PostResponseDto>> getAllPostsApi() {
        try {
            // SecurityContext에서 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Principal을 User 객체로 변환하여 userId 추출
            Object principal = authentication.getPrincipal();
            Long userId = null;

            if (principal instanceof User) {
                userId = ((User) principal).getId(); // User 객체에서 ID 가져오기
            } else {
            }

            // 인증된 사용자의 게시글 목록 가져오기
            List<PostResponseDto> postDto = postService.getAllPosts().stream()
                    .map(PostResponseDto::new) // PostResponseDto 생성자 활용
                    .toList();

            return ResponseEntity.ok(postDto); // 게시글 데이터를 JSON 형태로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 예외 발생 시 인증 실패 처리
        }
    }





    @GetMapping("/api1/{postId}")
    public String getPostDetailPage(@PathVariable Long postId, Model model) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        model.addAttribute("postId", postId);
        model.addAttribute("post", post);

        return "postdetail"; // 서버 측에서 HTML을 렌더링하여 반환
    }
    @GetMapping("/postedit/{postId}")
    public String getEditDetailPage(@PathVariable Long postId, Model model) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        model.addAttribute("postId", postId);

        return "postedit"; // 서버 측에서 HTML을 렌더링하여 반환
    }

    // 게시글 상세 조회 (JSON API)
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> getPostDetailApi(@PathVariable Long postId) {
        try {
            // SecurityContext에서 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증된 사용자 여부 확인
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // Principal을 User 객체로 변환하여 userId 추출
            Object principal = authentication.getPrincipal();
            Long userId = null;

            if (principal instanceof User) {
                userId = ((User) principal).getId(); // User 객체에서 ID 가져오기
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자입니다.");
            }

            // 게시글 조회
            Post post = postService.getPostById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

            // 조회수 증가
            postService.incrementViewCount(postId);

            PostResponseDto postDto = new PostResponseDto(post);
            return ResponseEntity.ok(postDto); // JSON 형태로 게시글 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }


    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> request) {
        User author = userService.findById(Long.parseLong(request.get("userId")));
        if (author == null) {
            return ResponseEntity.badRequest().body(null);  // 사용자가 없으면 400 에러 반환
        }

        // imageUrl은 선택적인 값이므로 null일 수 있음
        String imageUrl = request.getOrDefault("imageUrl", null);

        // Post 객체 생성
        Post post = Post.builder()
                .author(author)  // 작성자 설정
                .title(request.get("title"))  // 제목
                .content(request.get("content"))  // 내용
                .imageUrl(imageUrl)  // 이미지 URL (null일 수 있음)
                .commentCount(0)  // 댓글 수 초기화
                .viewCount(0)  // 조회 수 초기화
                .createdAt(LocalDateTime.now())  // 생성 시간
                .updatedAt(LocalDateTime.now())  // 업데이트 시간
                .build();

        // 게시글 저장 후 응답 반환
        return ResponseEntity.ok(postService.createPost(post));  // 성공적으로 저장된 게시글을 반환
    }


    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> postData, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println("게시글 수정 postid: " + postId); // 토큰 출력
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 토큰이 없거나 형식이 잘못됨
        }

        token = token.substring(7);  // "Bearer "를 제거하고 토큰만 추출
        System.out.println("게시글 수정 Extracted Token: " + token); // 토큰 출력

        Long userId = jwtUtil.extractUserId(token);
        System.out.println("게시글 수정 User ID: " + userId); // 사용자 ID 출력

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 유효한 userId가 아닐 경우
        }

        // Map에서 값을 추출하여 Post 객체 생성
        Post post = new Post();
        post.setTitle(postData.get("title"));
        post.setContent(postData.get("content"));

        // 게시글 수정
        Post updatedPost = postService.updatePost(postId, post, userId);

        if (updatedPost == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 없음
        }

        return ResponseEntity.ok(updatedPost);
    }




    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        // JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization").substring(7);  // "Bearer "를 제거하고 토큰만 추출
        Long userId = jwtUtil.extractUserId(token);

        // 게시글 삭제
        if (postService.deletePost(postId,userId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 없음
    }
}