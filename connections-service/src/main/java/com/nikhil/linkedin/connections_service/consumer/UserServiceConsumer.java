package com.nikhil.linkedin.connections_service.consumer;

import com.nikhil.linkedin.connections_service.entity.Person;
import com.nikhil.linkedin.connections_service.repository.PersonRepository;
import com.nikhil.linkedin.user_service.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceConsumer {

    private final PersonRepository personRepository;

    @KafkaListener(topics = "user-created-topic")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Creating Person node for new user: {}", event);

        if (personRepository.findByUserId(event.getUserId()).isPresent()) {
            log.info("Person already exists for userId: {}", event.getUserId());
            return;
        }

        Person person = new Person();
        person.setUserId(event.getUserId());
        person.setName(event.getName());
        personRepository.save(person);
        log.info("Person node created for userId: {}", event.getUserId());
    }
}
