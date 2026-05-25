package com.nikhil.linkedin.posts_service.service;

import com.nikhil.linkedin.posts_service.auth.UserContextHolder;
import com.nikhil.linkedin.posts_service.clients.ConnectionsClient;
import com.nikhil.linkedin.posts_service.dto.PersonDto;
import com.nikhil.linkedin.posts_service.dto.PostCreateRequestDto;
import com.nikhil.linkedin.posts_service.dto.PostDto;
import com.nikhil.linkedin.posts_service.entity.Post;
import com.nikhil.linkedin.posts_service.event.PostCreatedEvent;
import com.nikhil.linkedin.posts_service.exception.ResourceNotFoundException;
import com.nikhil.linkedin.posts_service.repository.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ConnectionsClient connectionsClient;
    @Mock
    private org.springframework.kafka.core.KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    @InjectMocks
    private PostsService postsService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void createPost_savesAndPublishesEvent() {
        UserContextHolder.setCurrentUserId(5L);

        PostCreateRequestDto request = new PostCreateRequestDto();
        request.setContent("Hello world");

        Post post = new Post();
        post.setContent("Hello world");

        Post saved = new Post();
        saved.setId(10L);
        saved.setContent("Hello world");
        saved.setUserId(5L);

        PostDto dto = new PostDto();
        dto.setId(10L);
        dto.setContent("Hello world");
        dto.setUserId(5L);

        when(modelMapper.map(request, Post.class)).thenReturn(post);
        when(postsRepository.save(any(Post.class))).thenReturn(saved);
        when(modelMapper.map(saved, PostDto.class)).thenReturn(dto);

        PostDto result = postsService.createPost(request);

        assertEquals(10L, result.getId());
        ArgumentCaptor<PostCreatedEvent> captor = ArgumentCaptor.forClass(PostCreatedEvent.class);
        verify(kafkaTemplate).send(eq("post-created-topic"), captor.capture());
        assertEquals(10L, captor.getValue().getPostId());
        assertEquals(5L, captor.getValue().getCreatorId());
    }

    @Test
    void getPostById_returnsPost() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("content");

        PostDto dto = new PostDto();
        dto.setId(1L);

        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(dto);

        assertEquals(1L, postsService.getPostById(1L).getId());
    }

    @Test
    void getPostById_throwsWhenNotFound() {
        when(postsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> postsService.getPostById(99L));
    }

    @Test
    void getFeedForCurrentUser_includesOwnAndConnectionPosts() {
        UserContextHolder.setCurrentUserId(1L);

        PersonDto connection = new PersonDto();
        connection.setUserId(2L);
        when(connectionsClient.getFirstConnections()).thenReturn(List.of(connection));

        Post post = new Post();
        post.setId(100L);
        post.setUserId(2L);
        post.setCreatedAt(LocalDateTime.now());

        PostDto dto = new PostDto();
        dto.setId(100L);

        when(postsRepository.findByUserIdInOrderByCreatedAtDesc(List.of(2L, 1L)))
                .thenReturn(List.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(dto);

        List<PostDto> feed = postsService.getFeedForCurrentUser();

        assertEquals(1, feed.size());
        assertEquals(100L, feed.get(0).getId());
    }
}
