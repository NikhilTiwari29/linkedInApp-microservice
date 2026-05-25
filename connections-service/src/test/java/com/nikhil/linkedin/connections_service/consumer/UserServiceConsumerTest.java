package com.nikhil.linkedin.connections_service.consumer;

import com.nikhil.linkedin.connections_service.entity.Person;
import com.nikhil.linkedin.connections_service.repository.PersonRepository;
import com.nikhil.linkedin.user_service.event.UserCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceConsumerTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private UserServiceConsumer userServiceConsumer;

    @Test
    void handleUserCreated_createsPersonWhenNotExists() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(42L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        when(personRepository.findByUserId(42L)).thenReturn(Optional.empty());

        userServiceConsumer.handleUserCreated(event);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(captor.capture());
        assertEquals(42L, captor.getValue().getUserId());
        assertEquals("Jane Doe", captor.getValue().getName());
    }

    @Test
    void handleUserCreated_skipsWhenPersonAlreadyExists() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(42L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        when(personRepository.findByUserId(42L)).thenReturn(Optional.of(new Person()));

        userServiceConsumer.handleUserCreated(event);

        verify(personRepository, never()).save(any());
    }
}
