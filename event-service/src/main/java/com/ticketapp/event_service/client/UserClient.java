// File: com.ticketapp.event_service.client.UserClient.java
package com.ticketapp.event_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{id}")
    Map<String, Object> getUserById(@PathVariable UUID id);
}
