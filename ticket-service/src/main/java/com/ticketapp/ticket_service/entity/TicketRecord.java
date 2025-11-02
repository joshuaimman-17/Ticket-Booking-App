package com.ticketapp.ticket_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class TicketRecord {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID eventId;

    // store file path as normal string (no LOB)
    @Column(nullable = false, length = 512)
    private String ticketPdfPath;

    // ensure Hibernate maps as VARCHAR instead of CLOB
    @Column(length = 2000, columnDefinition = "TEXT")
    private String qrText;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getTicketPdfPath() { return ticketPdfPath; }
    public void setTicketPdfPath(String ticketPdfPath) { this.ticketPdfPath = ticketPdfPath; }
    public String getQrText() { return qrText; }
    public void setQrText(String qrText) { this.qrText = qrText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
