package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service",url = "http://user-service:8081")
public interface UserClient {
    @PatchMapping("/users/admin/hosts/{id}/approve")
    void approveHost(@PathVariable UUID id);
}
