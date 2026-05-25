package com.nikhil.linkedin.uploader_service.controller;

import com.nikhil.linkedin.uploader_service.service.FileUploaderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploaderService fileUploaderService;

    @Test
    void uploadImage_returnsUrl() throws Exception {
        when(fileUploaderService.upload(any())).thenReturn("https://cdn.example.com/image.png");

        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "bytes".getBytes());

        mockMvc.perform(multipart("/file").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("https://cdn.example.com/image.png"));
    }
}
