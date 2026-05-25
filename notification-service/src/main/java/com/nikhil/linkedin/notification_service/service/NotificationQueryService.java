package com.nikhil.linkedin.notification_service.service;

import com.nikhil.linkedin.notification_service.auth.UserContextHolder;
import com.nikhil.linkedin.notification_service.dto.NotificationDto;
import com.nikhil.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public List<NotificationDto> getMyNotifications() {
        Long userId = UserContextHolder.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .toList();
    }
}
