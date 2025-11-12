package com.ticketapp.booking_service.dto;
import com.ticketapp.booking_service.entity.BookingStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingDTO {
    private UUID userId;
    private UUID eventId;
    private String ticketType;
    private int quantity;
    private BookingStatus status;
    private LocalDateTime holdExpiry;
    private String paymentStatus;

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getHoldExpiry() { return holdExpiry; }
    public void setHoldExpiry(LocalDateTime holdExpiry) { this.holdExpiry = holdExpiry; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
