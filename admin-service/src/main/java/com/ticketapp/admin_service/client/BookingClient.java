package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "booking-service")
public interface BookingClient {
    @GetMapping("/bookings/admin/stats")
    List<Object> getStats();
}
