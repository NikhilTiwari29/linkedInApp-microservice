package com.nikhil.linkedin.user_service.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String name;
    private String email;
}
