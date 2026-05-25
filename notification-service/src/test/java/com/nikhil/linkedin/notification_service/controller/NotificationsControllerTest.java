package com.nikhil.linkedin.notification_service.controller;

import com.nikhil.linkedin.notification_service.dto.NotificationDto;
import com.nikhil.linkedin.notification_service.service.NotificationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationsController.class)
class NotificationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationQueryService notificationQueryService;

    @Test
    void getMyNotifications_returnsInbox() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setId(1L);
        dto.setMessage("You have a new connection request");

        when(notificationQueryService.getMyNotifications()).thenReturn(List.of(dto));

        mockMvc.perform(get("/core/inbox")
                        .header("X-User-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("You have a new connection request"));
    }
}
