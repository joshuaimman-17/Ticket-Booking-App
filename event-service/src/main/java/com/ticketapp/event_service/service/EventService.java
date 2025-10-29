package com.ticketapp.event_service.service;

import com.ticketapp.event_service.entity.Event;
import com.ticketapp.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    // Get all events
    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    // Add event
    public Event addEvent(Event e) {
        return repo.save(e);
    }

    // Get event by ID
    public Event getEventById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    // Delete event
    public void deleteEvent(UUID id) {
        repo.deleteById(id);
    }

    // ✅ Update ticket availability (used by Booking Service)
    public Event updateTicketAvailability(UUID id, int ticketsSold) {
        Event event = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Check available ticket count
        int remainingTickets = event.getTotalTickets() - event.getTicketsSold();
        if (ticketsSold > remainingTickets) {
            throw new RuntimeException("Not enough tickets available for event ID: " + id);
        }

        event.setTicketsSold(event.getTicketsSold() + ticketsSold);
        return repo.save(event);
    }
}
