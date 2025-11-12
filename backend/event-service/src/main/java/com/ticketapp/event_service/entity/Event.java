package com.ticketapp.event_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "event_id", updatable = false, nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "event_type")
    private String eventType;

    private String status;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "ticket_price", nullable = false)
    private Double ticketPrice;

    @Column(name = "tickets_sold", nullable = false)
    private Integer ticketsSold = 0;

    public Event() {
    }

    public Event(String title, String description, String location,
                 LocalDateTime eventDate, String eventType, String status,
                 Integer totalTickets, Double ticketPrice) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.status = status;
        this.totalTickets = totalTickets;
        this.ticketPrice = ticketPrice;
        this.ticketsSold = 0;
    }

    // Getters and Setters

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Integer ticketsSold) {
        this.ticketsSold = ticketsSold;
    }
}
