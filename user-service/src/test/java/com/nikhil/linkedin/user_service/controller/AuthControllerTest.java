package com.nikhil.linkedin.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.linkedin.user_service.dto.LoginRequestDto;
import com.nikhil.linkedin.user_service.dto.SignupRequestDto;
import com.nikhil.linkedin.user_service.dto.UserDto;
import com.nikhil.linkedin.user_service.exception.BadRequestException;
import com.nikhil.linkedin.user_service.exception.GlobalExceptionHandler;
import com.nikhil.linkedin.user_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void signUp_returns201() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        when(authService.signUp(any())).thenReturn(userDto);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void signUp_returns400WhenUserExists() throws Exception {
        when(authService.signUp(any())).thenThrow(new BadRequestException("User already exists"));

        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("exists@example.com");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_returns400WhenValidationFails() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("not-an-email");
        request.setPassword("short");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returnsToken() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        when(authService.login(any())).thenReturn("jwt-token-value");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("jwt-token-value"));
    }
}
