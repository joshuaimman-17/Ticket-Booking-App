package com.ticketapp.admin_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {
    @PatchMapping("/users/admin/hosts/{id}/approve")
    void approveHost(@PathVariable UUID id);
}
