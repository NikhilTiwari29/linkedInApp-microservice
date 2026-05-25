package com.nikhil.linkedin.user_service.service;

import com.nikhil.linkedin.user_service.dto.LoginRequestDto;
import com.nikhil.linkedin.user_service.dto.SignupRequestDto;
import com.nikhil.linkedin.user_service.dto.UserDto;
import com.nikhil.linkedin.user_service.entity.User;
import com.nikhil.linkedin.user_service.event.UserCreatedEvent;
import com.nikhil.linkedin.user_service.exception.BadRequestException;
import com.nikhil.linkedin.user_service.exception.ResourceNotFoundException;
import com.nikhil.linkedin.user_service.repository.UserRepository;
import com.nikhil.linkedin.user_service.utils.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtService jwtService;
    @Mock
    private KafkaTemplate<Long, UserCreatedEvent> kafkaTemplate;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_savesUserAndPublishesEvent() {
        SignupRequestDto request = new SignupRequestDto();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Alice");
        savedUser.setEmail("alice@example.com");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("alice@example.com");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDto.class)).thenReturn(userDto);

        UserDto result = authService.signUp(request);

        assertEquals(1L, result.getId());
        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(kafkaTemplate).send(eq("user-created-topic"), eq(1L), eventCaptor.capture());
        assertEquals(1L, eventCaptor.getValue().getUserId());
        assertEquals("Alice", eventCaptor.getValue().getName());
    }

    @Test
    void signUp_throwsWhenEmailExists() {
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("exists@example.com");
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.signUp(request));
    }

    @Test
    void login_returnsTokenWhenCredentialsValid() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        User user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setPassword(PasswordUtil.hashPassword("secret123"));

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("jwt-token");

        String token = authService.login(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void login_throwsWhenUserNotFound() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_throwsWhenPasswordIncorrect() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("alice@example.com");
        request.setPassword("wrong");

        User user = new User();
        user.setEmail("alice@example.com");
        user.setPassword(PasswordUtil.hashPassword("secret123"));

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }
}
