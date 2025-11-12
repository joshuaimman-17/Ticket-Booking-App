package com.ticketapp.user_service.dto;
import com.ticketapp.user_service.entity.Role;


import java.util.UUID;


public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private boolean isVerified;


    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}