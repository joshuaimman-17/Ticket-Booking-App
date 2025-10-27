package com.ticketapp.user_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // 👇 Added password field for authentication
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean isVerified = false; // host verification

    private String idProofUrl;
    private String paypalEmail;
    private Float hostRating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "user_booking_history", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "booking_id")
    private List<String> bookingHistory = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_host_history", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "event_id")
    private List<String> hostHistory = new ArrayList<>();

    // ---------- Lifecycle Hooks ----------
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---------- Getters and Setters ----------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getIdProofUrl() { return idProofUrl; }
    public void setIdProofUrl(String idProofUrl) { this.idProofUrl = idProofUrl; }

    public String getPaypalEmail() { return paypalEmail; }
    public void setPaypalEmail(String paypalEmail) { this.paypalEmail = paypalEmail; }

    public Float getHostRating() { return hostRating; }
    public void setHostRating(Float hostRating) { this.hostRating = hostRating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getBookingHistory() { return bookingHistory; }
    public void setBookingHistory(List<String> bookingHistory) { this.bookingHistory = bookingHistory; }

    public List<String> getHostHistory() { return hostHistory; }
    public void setHostHistory(List<String> hostHistory) { this.hostHistory = hostHistory; }
}
