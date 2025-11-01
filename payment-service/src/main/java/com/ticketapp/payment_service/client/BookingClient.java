package com.ticketapp.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "booking-service",
        url = "http://localhost:8082"  // 👈 adjust port to your booking-service
)
public interface BookingClient {

    // ✅ Confirm booking after payment success
    @PutMapping("/bookings/{id}/confirm")
    void confirmBooking(@PathVariable("id") UUID bookingId,
                        @RequestParam(required = false) String paymentId);

    // ✅ Cancel booking if payment failed
    @PatchMapping("/bookings/{id}/cancel")
    void cancelBooking(@PathVariable("id") UUID bookingId);
}
