package com.ticketapp.user_service.dto;


public class HostApplicationDTO {
    private String idProofUrl;
    private String paypalEmail;


    // getters setters
    public String getIdProofUrl() { return idProofUrl; }
    public void setIdProofUrl(String idProofUrl) { this.idProofUrl = idProofUrl; }
    public String getPaypalEmail() { return paypalEmail; }
    public void setPaypalEmail(String paypalEmail) { this.paypalEmail = paypalEmail; }
}