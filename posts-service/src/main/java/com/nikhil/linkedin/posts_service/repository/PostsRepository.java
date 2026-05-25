package com.nikhil.linkedin.posts_service.repository;

import com.nikhil.linkedin.posts_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
}
