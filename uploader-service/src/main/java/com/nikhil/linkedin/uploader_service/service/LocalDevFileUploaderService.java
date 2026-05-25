package com.nikhil.linkedin.uploader_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnMissingBean(FileUploaderService.class)
@Slf4j
public class LocalDevFileUploaderService implements FileUploaderService {

    @Override
    public String upload(MultipartFile file) throws IOException {
        String url = "local-dev://uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        log.warn("No cloud storage configured. Returning mock URL: {}", url);
        return url;
    }
}
