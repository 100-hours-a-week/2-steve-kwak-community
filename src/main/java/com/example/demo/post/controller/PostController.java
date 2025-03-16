package com.example.demo.post.controller;

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
        User author = new User();
        author.setId(Long.parseLong(request.get("userId")));

        Post post = Post.builder()
                .author(author)
                .title(request.get("title"))
                .content(request.get("content"))
                .imageUrl(request.get("imageUrl"))
                .likeCount(0)
                .commentCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(postService.createPost(post));
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
