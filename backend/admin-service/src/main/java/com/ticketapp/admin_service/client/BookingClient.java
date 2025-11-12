package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "booking-service",url = "${BOOKING_SERVICE_URL:http://localhost:8082}")
public interface BookingClient {
    @GetMapping("/bookings/admin/stats")
    Map<String , Object> getStats();
}
