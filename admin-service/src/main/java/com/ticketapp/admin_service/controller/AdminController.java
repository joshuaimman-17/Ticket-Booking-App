package com.ticketapp.admin_service.controller;

import com.ticketapp.admin_service.client.BookingClient;
import com.ticketapp.admin_service.client.EventClient;
import com.ticketapp.admin_service.client.UserClient;
import com.ticketapp.admin_service.dto.HostApprovalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserClient userClient;
    private final EventClient eventClient;
    private final BookingClient bookingClient;

    public AdminController(UserClient userClient, EventClient eventClient, BookingClient bookingClient) {
        this.userClient = userClient;
        this.eventClient = eventClient;
        this.bookingClient = bookingClient;
    }

    // ✅ Example: Gateway injects headers like X-User-Role: ADMIN
    private boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    @GetMapping("/hosts/pending")
    public ResponseEntity<?> getPendingHosts(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        return ResponseEntity.ok("Call user-service /admin/hosts/pending");
    }

    @PatchMapping("/hosts/{id}/approve")
    public ResponseEntity<?> approveHost(
            @PathVariable UUID id,
            @RequestBody HostApprovalRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        userClient.approveHost(id);
        return ResponseEntity.ok("Host approved");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id,
                                         @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        eventClient.deleteEvent(id);
        return ResponseEntity.ok("Event deleted");
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        try {
            var s = bookingClient.getStats();
            return ResponseEntity.ok(s);
        } catch (Exception ex) {
            return ResponseEntity.ok("Booking stats unavailable: " + ex.getMessage());
        }
    }
}
