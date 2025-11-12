package com.ticketapp.booking_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue
    private UUID bookingId;

    private UUID userId;
    private UUID eventId;
    private String ticketType;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime holdExpiry;
    private String paymentStatus;
    private String paymentId;
    private boolean cancellationAllowed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
