package com.example.demo.post.controller;

import com.example.demo.login.service.UserService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import com.example.demo.login.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 게시글 목록 조회
    @GetMapping
    public String getAllPostsPage() {
        return "posts"; // templates 폴더에서 posts.html 반환
    }
    @GetMapping("/makepost")
    public  String getMakePostPage(){
        return "makepost";
    }

    // 게시글 목록 조회 (데이터 API)
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<PostResponseDto>> getAllPostsApi() {
        List<PostResponseDto> postDto = postService.getAllPosts().stream()
                .map(PostResponseDto::new)
                .toList();
        return ResponseEntity.ok(postDto); // 게시글 데이터를 JSON 형태로 반환
    }

    // 게시글 상세 조회 (DTO 적용)
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId)
                .map(post -> ResponseEntity.ok(new PostResponseDto(post)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> request) {
        User author = userService.findById(Long.parseLong(request.get("userId")));
        if (author == null) {
            return ResponseEntity.badRequest().body(null);  // 사용자가 없으면 400 에러 반환
        }
        System.out.println("📌 받은 데이터: " + request);

        // imageUrl은 선택적인 값이므로 null일 수 있음
        String imageUrl = request.getOrDefault("imageUrl", null);

        // Post 객체 생성
        Post post = Post.builder()
                .author(author)  // 작성자 설정
                .title(request.get("title"))  // 제목
                .content(request.get("content"))  // 내용
                .imageUrl(imageUrl)  // 이미지 URL (null일 수 있음)
                .likeCount(0)  // 좋아요 수 초기화
                .commentCount(0)  // 댓글 수 초기화
                .viewCount(0)  // 조회 수 초기화
                .createdAt(LocalDateTime.now())  // 생성 시간
                .updatedAt(LocalDateTime.now())  // 업데이트 시간
                .build();

        System.out.println("📌 저장될 데이터: " + post);

        // 게시글 저장 후 응답 반환
        return ResponseEntity.ok(postService.createPost(post));  // 성공적으로 저장된 게시글을 반환
    }


    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        return postService.updatePost(postId, request.get("title"), request.get("content"), request.get("imageUrl"))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        if (postService.deletePost(postId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
