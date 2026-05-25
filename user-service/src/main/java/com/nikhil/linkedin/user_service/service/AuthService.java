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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> kafkaTemplate;

    public UserDto signUp(SignupRequestDto signupRequestDto) {
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists) {
            throw new BadRequestException("User already exists, cannot signup again.");
        }

        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));

        User savedUser = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
        kafkaTemplate.send("user-created-topic", savedUser.getId(), userCreatedEvent);
        log.info("Published UserCreatedEvent for userId: {}", savedUser.getId());

        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+loginRequestDto.getEmail()));

        boolean isPasswordMatch = PasswordUtil.checkPassword(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect password");
        }

        return jwtService.generateAccessToken(user);
    }
}
