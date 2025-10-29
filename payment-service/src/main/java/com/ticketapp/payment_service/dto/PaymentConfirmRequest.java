package com.ticketapp.payment_service.dto;

import java.util.UUID;

public class PaymentConfirmRequest {
    private UUID paymentRecordId;
    private String providerPaymentId;
    private String status;

    public UUID getPaymentRecordId() { return paymentRecordId; }
    public void setPaymentRecordId(UUID paymentRecordId) { this.paymentRecordId = paymentRecordId; }
    public String getProviderPaymentId() { return providerPaymentId; }
    public void setProviderPaymentId(String providerPaymentId) { this.providerPaymentId = providerPaymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
