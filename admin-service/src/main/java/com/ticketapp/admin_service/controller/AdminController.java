package com.ticketapp.admin_service.controller;

import com.ticketapp.admin_service.client.BookingClient;
import com.ticketapp.admin_service.client.EventClient;
import com.ticketapp.admin_service.client.UserClient;
import com.ticketapp.admin_service.dto.AdminLoginRequest;
import com.ticketapp.admin_service.dto.AdminSignupRequest;
import com.ticketapp.admin_service.dto.HostApprovalRequest;
import com.ticketapp.admin_service.entity.Admin;
import com.ticketapp.admin_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final BookingClient bookingClient;

    public AdminController(AdminService adminService,
                           UserClient userClient,
                           EventClient eventClient,
                           BookingClient bookingClient) {
        this.adminService = adminService;
        this.userClient = userClient;
        this.eventClient = eventClient;
        this.bookingClient = bookingClient;
    }

    // ===================================
    // 🔹 AUTH ENDPOINTS
    // ===================================

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AdminSignupRequest req) {
        Optional<Admin> existing = adminService.findByEmail(req.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.status(400).body(Map.of("error", "Email already registered"));
        }

        Admin admin = new Admin();
        admin.setEmail(req.getEmail());
        admin.setName(req.getName());
        Admin saved = adminService.signup(admin, req.getPassword());

        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "name", saved.getName(),
                "message", "Admin account created successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest req) {
        Optional<Admin> adminOpt = adminService.findByEmail(req.getEmail());
        if (adminOpt.isEmpty() || !adminService.validatePassword(adminOpt.get(), req.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        Admin admin = adminOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", admin.getId(),
                "email", admin.getEmail(),
                "name", admin.getName(),
                "role", "ADMIN",
                "message", "Login successful"
        ));
    }

    // ===================================
    // 🔹 ADMIN FEATURES
    // ===================================

    private boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    // ✅ Fetch pending hosts from user-service
    @GetMapping("/hosts/pending")
    public ResponseEntity<?> getPendingHosts(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            var pendingHosts = userClient.getPendingHosts();
            return ResponseEntity.ok(pendingHosts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch pending hosts",
                    "details", e.getMessage()
            ));
        }
    }

    // ✅ Approve host
    @PatchMapping("/hosts/{id}/approve")
    public ResponseEntity<?> approveHost(
            @PathVariable UUID id,
            @RequestBody(required = false) HostApprovalRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            userClient.approveHost(id);
            return ResponseEntity.ok(Map.of("message", "Host approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to approve host",
                    "details", e.getMessage()
            ));
        }
    }

    // ✅ Reject host
    @PatchMapping("/hosts/{id}/reject")
    public ResponseEntity<?> rejectHost(
            @PathVariable UUID id,
            @RequestBody(required = false) HostApprovalRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            userClient.rejectHost(id);
            return ResponseEntity.ok(Map.of("message", "Host rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to reject host",
                    "details", e.getMessage()
            ));
        }
    }

    // ✅ Delete event
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            eventClient.deleteEvent(id);
            return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to delete event",
                    "details", e.getMessage()
            ));
        }
    }

    // ✅ Booking stats from booking-service
    @GetMapping("/stats")
    public ResponseEntity<?> stats(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            var stats = bookingClient.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.ok(Map.of(
                    "error", "Booking stats unavailable",
                    "details", ex.getMessage()
            ));
        }
    }
    // Fetch all approved hosts
    @GetMapping("/hosts/approved")
    public ResponseEntity<?> getApprovedHosts(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        try {
            var hosts = userClient.getApprovedHosts();
            return ResponseEntity.ok(hosts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch approved hosts",
                    "details", e.getMessage()
            ));
        }
    }

}
