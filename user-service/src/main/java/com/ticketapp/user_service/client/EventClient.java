package com.ticketapp.user_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;
import java.util.UUID;


@FeignClient(name = "event-service", url = "${EVENT_SERVICE_URL:http://localhost:8080}")
public interface EventClient {
    @GetMapping("/events/host/{hostId}")
    List<Object> getEventsByHost(@PathVariable UUID hostId);
}