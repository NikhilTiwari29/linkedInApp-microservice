package com.nikhil.linkedin.uploader_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalDevFileUploaderServiceTest {

    private final LocalDevFileUploaderService uploader = new LocalDevFileUploaderService();

    @Test
    void upload_returnsLocalDevUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "data".getBytes());

        String url = uploader.upload(file);

        assertTrue(url.startsWith("local-dev://uploads/"));
        assertTrue(url.contains("photo.png"));
    }
}
