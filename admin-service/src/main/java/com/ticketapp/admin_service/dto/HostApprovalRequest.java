package com.ticketapp.admin_service.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostApprovalRequest {
    private String action; // "approve" or "reject"
}
