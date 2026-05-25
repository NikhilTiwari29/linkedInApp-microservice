package com.nikhil.linkedin.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.linkedin.user_service.dto.LoginRequestDto;
import com.nikhil.linkedin.user_service.dto.SignupRequestDto;
import com.nikhil.linkedin.user_service.event.UserCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTemplate<Long, UserCreatedEvent> kafkaTemplate;

    @Test
    void signupAndLogin_roundTrip() throws Exception {
        SignupRequestDto signup = new SignupRequestDto();
        signup.setName("Integration User");
        signup.setEmail("integration." + UUID.randomUUID() + "@example.com");
        signup.setPassword("secret12345");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(signup.getEmail()));

        LoginRequestDto login = new LoginRequestDto();
        login.setEmail(signup.getEmail());
        login.setPassword("secret12345");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }
}
