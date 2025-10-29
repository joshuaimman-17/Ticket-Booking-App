package com.ticketapp.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PaymentRequest {
    @NotNull
    private UUID bookingId;
    @NotNull
    private UUID userId;
    @NotNull
    private Double amount;
    private String currency = "INR";

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
