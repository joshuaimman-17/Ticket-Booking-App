package com.ticketapp.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "event-service", url = "${EVENT_SERVICE_URL:http://localhost:8083}")
public interface EventClient {

    // Matches EventService.updateTicketAvailability(UUID id, int ticketsSold)
    @PatchMapping("/events/{eventId}/tickets")
    void updateTicketAvailability(
            @PathVariable UUID eventId,
            @RequestParam("ticketsSold") int ticketsSold
    );
}
