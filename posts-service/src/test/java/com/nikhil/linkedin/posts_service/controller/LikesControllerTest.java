package com.nikhil.linkedin.posts_service.controller;

import com.nikhil.linkedin.posts_service.service.PostLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikesController.class)
class LikesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostLikeService postLikeService;

    @Test
    void likePost_returns204() throws Exception {
        doNothing().when(postLikeService).likePost(10L);

        mockMvc.perform(post("/likes/10"))
                .andExpect(status().isNoContent());

        verify(postLikeService).likePost(10L);
    }

    @Test
    void unlikePost_returns204() throws Exception {
        doNothing().when(postLikeService).unlikePost(10L);

        mockMvc.perform(delete("/likes/10"))
                .andExpect(status().isNoContent());

        verify(postLikeService).unlikePost(10L);
    }
}
