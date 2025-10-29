package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "event-service",url = "${EVENT_SERVICE_URL:http://localhost:8083}")
public interface EventClient {
    @DeleteMapping("/events/{id}")
    void deleteEvent(@PathVariable("id") UUID id);
}
