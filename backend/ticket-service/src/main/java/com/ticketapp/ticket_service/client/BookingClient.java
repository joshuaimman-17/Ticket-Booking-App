package com.ticketapp.ticket_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "booking-service", url = "${BOOKING_SERVICE_URL:http://localhost:8082}")
public interface BookingClient {
    @GetMapping("/bookings/{bookingId}")
    Map<String, Object> getBooking(@PathVariable("bookingId") UUID bookingId);
}

