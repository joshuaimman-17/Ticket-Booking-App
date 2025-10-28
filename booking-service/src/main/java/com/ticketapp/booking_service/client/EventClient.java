package com.ticketapp.booking_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "event-service", url = "${EVENT_SERVICE_URL:http://localhost:8080}")
public interface EventClient {
    @PatchMapping("/events/{eventId}/tickets")
    void updateTicketAvailability(@PathVariable UUID eventId, @RequestBody Map<String, Integer> ticketsDiff);
}
