package com.ticketapp.user_service.controller;

import com.ticketapp.user_service.dto.AuthRequest;
import com.ticketapp.user_service.dto.AuthResponse;
import com.ticketapp.user_service.dto.HostApplicationDTO;
import com.ticketapp.user_service.entity.User;
import com.ticketapp.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ---------------- AUTH ----------------

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        User saved = service.register(user);
        return ResponseEntity.created(URI.create("/users/" + saved.getId())).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        var userOpt = service.login(req.getEmail(), req.getPassword());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        var u = userOpt.get();
        AuthResponse res = new AuthResponse();
        res.setUserId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setRole(u.getRole().name());
        return ResponseEntity.ok(res);
    }


    // ---------------- HOST MANAGEMENT ----------------

    @PostMapping("/{id}/host/apply")
    public ResponseEntity<User> applyHost(@PathVariable UUID id, @RequestBody HostApplicationDTO dto) {
        return ResponseEntity.ok(service.applyHost(id, dto));
    }

    @PatchMapping("/admin/hosts/{id}/approve")
    public ResponseEntity<User> approveHost(@PathVariable UUID id) {
        return ResponseEntity.ok(service.approveHost(id));
    }

    @PatchMapping("/admin/hosts/{id}/reject")
    public ResponseEntity<User> rejectHost(@PathVariable UUID id) {
        return ResponseEntity.ok(service.rejectHost(id));
    }

    @GetMapping("/admin/hosts/pending")
    public ResponseEntity<List<User>> getPendingHosts() {
        return ResponseEntity.ok(service.getPendingHosts());
    }

    // ---------------- USER DETAILS ----------------

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
