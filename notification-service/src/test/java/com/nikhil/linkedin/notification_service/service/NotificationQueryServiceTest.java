package com.nikhil.linkedin.notification_service.service;

import com.nikhil.linkedin.notification_service.auth.UserContextHolder;
import com.nikhil.linkedin.notification_service.dto.NotificationDto;
import com.nikhil.linkedin.notification_service.entity.Notification;
import com.nikhil.linkedin.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void getMyNotifications_returnsMappedDtos() {
        UserContextHolder.setCurrentUserId(5L);

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUserId(5L);
        notification.setMessage("Test");
        notification.setCreatedAt(LocalDateTime.now());

        NotificationDto dto = new NotificationDto();
        dto.setId(1L);
        dto.setMessage("Test");

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(dto);

        List<NotificationDto> result = notificationQueryService.getMyNotifications();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getMessage());
    }
}
