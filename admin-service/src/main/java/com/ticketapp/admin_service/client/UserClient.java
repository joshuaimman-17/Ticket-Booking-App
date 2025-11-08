package com.ticketapp.admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL:http://localhost:8081}")
public interface UserClient {

    @GetMapping("/users/admin/hosts/pending")
    List<Map<String, Object>> getPendingHosts();

    @PutMapping("/users/admin/hosts/{id}/approve")
    void approveHost(@PathVariable UUID id);
    @GetMapping("/users/admin/hosts/approved")
    List<Map<String, Object>> getApprovedHosts();

    @PutMapping("/users/admin/hosts/{id}/reject")
    void rejectHost(@PathVariable UUID id);
}
