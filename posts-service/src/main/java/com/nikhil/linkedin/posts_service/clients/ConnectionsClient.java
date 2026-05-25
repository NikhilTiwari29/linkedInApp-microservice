package com.nikhil.linkedin.posts_service.clients;

import com.nikhil.linkedin.posts_service.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "connections-service", path = "/connections", url = "${CONNECTIONS_SERVICE_URI:}")
public interface ConnectionsClient {

    @GetMapping("/core/first-degree")
    List<PersonDto> getFirstConnections();

}
