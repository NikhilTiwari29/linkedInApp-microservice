package com.nikhil.linkedin.posts_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.linkedin.posts_service.dto.PostCreateRequestDto;
import com.nikhil.linkedin.posts_service.dto.PostDto;
import com.nikhil.linkedin.posts_service.exception.GlobalExceptionHandler;
import com.nikhil.linkedin.posts_service.exception.ResourceNotFoundException;
import com.nikhil.linkedin.posts_service.service.PostsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostsController.class)
@Import(GlobalExceptionHandler.class)
class PostsControllerTest {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String TEST_USER_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostsService postsService;

    @Test
    void createPost_returns201() throws Exception {
        PostCreateRequestDto request = new PostCreateRequestDto();
        request.setContent("Hello");

        PostDto dto = new PostDto();
        dto.setId(1L);
        dto.setContent("Hello");

        when(postsService.createPost(any())).thenReturn(dto);

        mockMvc.perform(post("/core")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPost_returns200() throws Exception {
        PostDto dto = new PostDto();
        dto.setId(5L);
        dto.setContent("Post content");

        when(postsService.getPostById(5L)).thenReturn(dto);

        mockMvc.perform(get("/core/5")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Post content"));
    }

    @Test
    void getPost_returns404WhenNotFound() throws Exception {
        when(postsService.getPostById(99L))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(get("/core/99")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost_returns400WhenContentBlank() throws Exception {
        PostCreateRequestDto request = new PostCreateRequestDto();
        request.setContent("");

        mockMvc.perform(post("/core")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFeed_returnsPosts() throws Exception {
        PostDto dto = new PostDto();
        dto.setId(1L);
        when(postsService.getFeedForCurrentUser()).thenReturn(List.of(dto));

        mockMvc.perform(get("/core/feed")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
