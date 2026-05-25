package com.nikhil.linkedin.posts_service.service;

import com.nikhil.linkedin.posts_service.auth.UserContextHolder;
import com.nikhil.linkedin.posts_service.entity.Post;
import com.nikhil.linkedin.posts_service.event.PostLikedEvent;
import com.nikhil.linkedin.posts_service.exception.BadRequestException;
import com.nikhil.linkedin.posts_service.exception.ResourceNotFoundException;
import com.nikhil.linkedin.posts_service.repository.PostLikeRepository;
import com.nikhil.linkedin.posts_service.repository.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostsRepository postsRepository;
    @Mock
    private KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    @InjectMocks
    private PostLikeService postLikeService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void likePost_savesLikeAndPublishesEvent() {
        UserContextHolder.setCurrentUserId(3L);

        Post post = new Post();
        post.setId(7L);
        post.setUserId(1L);

        when(postsRepository.findById(7L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByUserIdAndPostId(3L, 7L)).thenReturn(false);

        postLikeService.likePost(7L);

        verify(postLikeRepository).save(any());
        verify(kafkaTemplate).send(eq("post-liked-topic"), eq(7L), any(PostLikedEvent.class));
    }

    @Test
    void likePost_throwsWhenAlreadyLiked() {
        UserContextHolder.setCurrentUserId(3L);
        Post post = new Post();
        post.setId(7L);
        when(postsRepository.findById(7L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByUserIdAndPostId(3L, 7L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> postLikeService.likePost(7L));
        verify(postLikeRepository, never()).save(any());
    }

    @Test
    void likePost_throwsWhenPostNotFound() {
        UserContextHolder.setCurrentUserId(3L);
        when(postsRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postLikeService.likePost(7L));
    }

    @Test
    void unlikePost_deletesLike() {
        UserContextHolder.setCurrentUserId(3L);
        when(postsRepository.existsById(7L)).thenReturn(true);
        when(postLikeRepository.existsByUserIdAndPostId(3L, 7L)).thenReturn(true);

        postLikeService.unlikePost(7L);

        verify(postLikeRepository).deleteByUserIdAndPostId(3L, 7L);
    }

    @Test
    void unlikePost_throwsWhenNotLiked() {
        UserContextHolder.setCurrentUserId(3L);
        when(postsRepository.existsById(7L)).thenReturn(true);
        when(postLikeRepository.existsByUserIdAndPostId(3L, 7L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> postLikeService.unlikePost(7L));
    }
}
