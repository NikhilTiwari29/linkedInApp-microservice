package com.nikhil.linkedin.notification_service.consumer;

import com.nikhil.linkedin.notification_service.clients.ConnectionsClient;
import com.nikhil.linkedin.notification_service.dto.PersonDto;
import com.nikhil.linkedin.notification_service.service.SendNotification;
import com.nikhil.linkedin.posts_service.event.PostCreatedEvent;
import com.nikhil.linkedin.posts_service.event.PostLikedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostsServiceConsumerTest {

    @Mock
    private ConnectionsClient connectionsClient;
    @Mock
    private SendNotification sendNotification;

    @InjectMocks
    private PostsServiceConsumer postsServiceConsumer;

    @Test
    void handlePostCreated_notifiesConnections() {
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(10L)
                .creatorId(1L)
                .content("Hello")
                .build();

        PersonDto connection = new PersonDto();
        connection.setUserId(2L);
        when(connectionsClient.getFirstConnections(1L)).thenReturn(List.of(connection));

        postsServiceConsumer.handlePostCreated(event);

        verify(sendNotification).send(eq(2L), contains("connection 1"));
    }

    @Test
    void handlePostLiked_notifiesCreator() {
        PostLikedEvent event = PostLikedEvent.builder()
                .postId(10L)
                .creatorId(1L)
                .likedByUserId(3L)
                .build();

        postsServiceConsumer.handlePostLiked(event);

        verify(sendNotification).send(eq(1L), contains("has been liked"));
    }
}
