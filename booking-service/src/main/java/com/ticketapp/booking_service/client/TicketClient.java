package com.ticketapp.booking_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "ticket-service", url = "${TICKET_SERVICE_URL:http://localhost:8084}")
public interface TicketClient {
    @PostMapping("/tickets/generate")
    void generateTicket(@RequestParam UUID bookingId);
}
