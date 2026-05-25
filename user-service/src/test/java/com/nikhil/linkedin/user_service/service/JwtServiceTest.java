package com.nikhil.linkedin.user_service.service;

import com.nikhil.linkedin.user_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey",
                "test-secret-key-for-unit-tests-min-32-chars");
    }

    @Test
    void generateAndParseToken_returnsUserId() {
        User user = new User();
        user.setId(99L);
        user.setEmail("test@example.com");

        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);

        Long userId = jwtService.getUserIdFromToken(token);
        assertEquals(99L, userId);
    }
}
