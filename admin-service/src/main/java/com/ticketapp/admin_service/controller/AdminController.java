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

    // ===============================
    // 🔹 AUTH ENDPOINTS
    // ===============================

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

    // ===============================
    // 🔹 ADMIN FEATURES
    // ===============================

    private boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    // ✅ Fetch pending hosts from user-service
    @GetMapping("/hosts/pending")
    public ResponseEntity<?> getPendingHosts(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        return ResponseEntity.ok("Call user-service /admin/hosts/pending");
    }


    // ✅ Approve host
    @PatchMapping("/hosts/{id}/approve")
    public ResponseEntity<?> approveHost(
            @PathVariable UUID id,
            @RequestBody(required = false) HostApprovalRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        try {
            userClient.approveHost(id);
            return ResponseEntity.ok("Host approved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to approve host: " + e.getMessage());
        }
    }

    // ✅ Delete event
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id,
                                         @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        try {
            eventClient.deleteEvent(id);
            return ResponseEntity.ok("Event deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete event: " + e.getMessage());
        }
    }

    // ✅ Booking stats from booking-service
    @GetMapping("/stats")
    public ResponseEntity<?> stats(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!isAdmin(role)) return ResponseEntity.status(403).body("Access denied");
        try {
            var stats = bookingClient.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.ok("Booking stats unavailable: " + ex.getMessage());
        }
    }
}
