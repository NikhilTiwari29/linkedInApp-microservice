package com.nikhil.linkedin.notification_service.service;

import com.nikhil.linkedin.notification_service.entity.Notification;
import com.nikhil.linkedin.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendNotificationTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private SendNotification sendNotification;

    @Test
    void send_persistsNotification() {
        sendNotification.send(42L, "Hello!");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(42L, captor.getValue().getUserId());
        assertEquals("Hello!", captor.getValue().getMessage());
    }
}
