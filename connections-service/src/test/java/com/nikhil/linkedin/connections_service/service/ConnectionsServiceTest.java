package com.nikhil.linkedin.connections_service.service;

import com.nikhil.linkedin.connections_service.auth.UserContextHolder;
import com.nikhil.linkedin.connections_service.entity.Person;
import com.nikhil.linkedin.connections_service.event.AcceptConnectionRequestEvent;
import com.nikhil.linkedin.connections_service.event.SendConnectionRequestEvent;
import com.nikhil.linkedin.connections_service.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionsServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    private KafkaTemplate<Long, SendConnectionRequestEvent> sendRequestKafkaTemplate;
    @Mock
    private KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptRequestKafkaTemplate;

    private ConnectionsService connectionsService;

    @BeforeEach
    void setUp() {
        connectionsService = new ConnectionsService(
                personRepository, sendRequestKafkaTemplate, acceptRequestKafkaTemplate);
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void getFirstDegreeConnections_returnsConnections() {
        UserContextHolder.setCurrentUserId(1L);
        Person person = new Person();
        person.setUserId(2L);
        when(personRepository.getFirstDegreeConnections(1L)).thenReturn(List.of(person));

        List<Person> result = connectionsService.getFirstDegreeConnections();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());
    }

    @Test
    void sendConnectionRequest_succeeds() {
        UserContextHolder.setCurrentUserId(1L);
        when(personRepository.connectionRequestExists(1L, 2L)).thenReturn(false);
        when(personRepository.alreadyConnected(1L, 2L)).thenReturn(false);

        Boolean result = connectionsService.sendConnectionRequest(2L);

        assertTrue(result);
        verify(personRepository).addConnectionRequest(1L, 2L);
        verify(sendRequestKafkaTemplate).send(eq("send-connection-request-topic"), any());
    }

    @Test
    void sendConnectionRequest_throwsWhenSameUser() {
        UserContextHolder.setCurrentUserId(1L);
        assertThrows(RuntimeException.class, () -> connectionsService.sendConnectionRequest(1L));
    }

    @Test
    void sendConnectionRequest_throwsWhenRequestExists() {
        UserContextHolder.setCurrentUserId(1L);
        when(personRepository.connectionRequestExists(1L, 2L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> connectionsService.sendConnectionRequest(2L));
    }

    @Test
    void acceptConnectionRequest_succeeds() {
        UserContextHolder.setCurrentUserId(2L);
        when(personRepository.connectionRequestExists(1L, 2L)).thenReturn(true);

        Boolean result = connectionsService.acceptConnectionRequest(1L);

        assertTrue(result);
        verify(personRepository).acceptConnectionRequest(1L, 2L);
        verify(acceptRequestKafkaTemplate).send(eq("accept-connection-request-topic"), any());
    }

    @Test
    void rejectConnectionRequest_succeeds() {
        UserContextHolder.setCurrentUserId(2L);
        when(personRepository.connectionRequestExists(1L, 2L)).thenReturn(true);

        Boolean result = connectionsService.rejectConnectionRequest(1L);

        assertTrue(result);
        verify(personRepository).rejectConnectionRequest(1L, 2L);
    }
}
