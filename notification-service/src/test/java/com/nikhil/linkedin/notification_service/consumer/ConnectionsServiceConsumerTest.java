package com.nikhil.linkedin.notification_service.consumer;

import com.nikhil.linkedin.connections_service.event.AcceptConnectionRequestEvent;
import com.nikhil.linkedin.connections_service.event.SendConnectionRequestEvent;
import com.nikhil.linkedin.notification_service.service.SendNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConnectionsServiceConsumerTest {

    @Mock
    private SendNotification sendNotification;

    @InjectMocks
    private ConnectionsServiceConsumer connectionsServiceConsumer;

    @Test
    void handleSendConnectionRequest_notifiesReceiver() {
        SendConnectionRequestEvent event = SendConnectionRequestEvent.builder()
                .senderId(1L)
                .receiverId(2L)
                .build();

        connectionsServiceConsumer.handleSendConnectionRequest(event);

        verify(sendNotification).send(eq(2L), contains("connection request"));
    }

    @Test
    void handleAcceptConnectionRequest_notifiesSender() {
        AcceptConnectionRequestEvent event = AcceptConnectionRequestEvent.builder()
                .senderId(1L)
                .receiverId(2L)
                .build();

        connectionsServiceConsumer.handleAcceptConnectionRequest(event);

        verify(sendNotification).send(eq(1L), contains("accepted"));
    }
}
