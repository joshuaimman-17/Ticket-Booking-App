package com.ticketapp.payment_service.dto;

public class PaymentResponse {
    private String message;
    private String paymentId;
    private double finalAmount;

    public PaymentResponse(String message, String paymentId, double finalAmount) {
        this.message = message;
        this.paymentId = paymentId;
        this.finalAmount = finalAmount;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
}
