package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service",url = "http://user-service:8081")
public interface UserClient {
    @GetMapping("/admin/hosts/pending")
    List<Map<String, Object>> getPendingHosts();

    @PatchMapping("/admin/hosts/{id}/approve")
    void approveHost(@PathVariable UUID id);

    @PatchMapping("/admin/hosts/{id}/reject")
    void rejectHost(@PathVariable UUID id);
}