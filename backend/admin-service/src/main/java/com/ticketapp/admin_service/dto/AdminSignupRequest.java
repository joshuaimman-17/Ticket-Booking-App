package com.ticketapp.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSignupRequest {
    private String email;
    private String password;
    private String name;
}
