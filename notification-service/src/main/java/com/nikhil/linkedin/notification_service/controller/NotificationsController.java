package com.nikhil.linkedin.notification_service.controller;

import com.nikhil.linkedin.notification_service.dto.NotificationDto;
import com.nikhil.linkedin.notification_service.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping("/inbox")
    public ResponseEntity<List<NotificationDto>> getMyNotifications() {
        return ResponseEntity.ok(notificationQueryService.getMyNotifications());
    }
}
