package com.example.demo.post.repository;

import com.example.demo.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByAuthorId(Long userId);

    List<Post> findByTitleContaining(String keyword);
}
