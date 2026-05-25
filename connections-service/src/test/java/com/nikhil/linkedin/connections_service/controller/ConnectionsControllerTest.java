package com.nikhil.linkedin.connections_service.controller;

import com.nikhil.linkedin.connections_service.entity.Person;
import com.nikhil.linkedin.connections_service.exception.GlobalExceptionHandler;
import com.nikhil.linkedin.connections_service.service.ConnectionsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConnectionsController.class)
@Import(GlobalExceptionHandler.class)
class ConnectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionsService connectionsService;

    @Test
    void getFirstConnections_returnsList() throws Exception {
        Person person = new Person();
        person.setUserId(2L);
        person.setName("Bob");
        when(connectionsService.getFirstDegreeConnections()).thenReturn(List.of(person));

        mockMvc.perform(get("/core/first-degree")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    void sendConnectionRequest_returnsTrue() throws Exception {
        when(connectionsService.sendConnectionRequest(2L)).thenReturn(true);

        mockMvc.perform(post("/core/request/2")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
