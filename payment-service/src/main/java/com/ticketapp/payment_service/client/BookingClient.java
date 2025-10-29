package com.ticketapp.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "booking-service")
public interface BookingClient {
    @PatchMapping("/bookings/{id}/confirm")
    void confirmBooking(@PathVariable("id") UUID bookingId,
                        @RequestParam(required = false) String paymentId);
}
