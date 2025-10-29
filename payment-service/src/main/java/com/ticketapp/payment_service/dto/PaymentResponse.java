package com.ticketapp.payment_service.dto;

public class PaymentResponse {
    private String providerCheckoutUrl;
    private String paymentId;

    public PaymentResponse(String providerCheckoutUrl, String paymentId) {
        this.providerCheckoutUrl = providerCheckoutUrl;
        this.paymentId = paymentId;
    }

    public String getProviderCheckoutUrl() { return providerCheckoutUrl; }
    public void setProviderCheckoutUrl(String providerCheckoutUrl) { this.providerCheckoutUrl = providerCheckoutUrl; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
