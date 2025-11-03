package com.ticketapp.event_service.service;

import com.ticketapp.event_service.client.UserClient;
import com.ticketapp.event_service.entity.Event;
import com.ticketapp.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository repo;
    private final UserClient userClient;

    public EventService(EventRepository repo, UserClient userClient) {
        this.repo = repo;
        this.userClient = userClient;
    }

    // Get all events
    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    // ✅ Add event (only allowed for HOST users)
    public Event addEvent(UUID userId, Event e) {
        Map<String, Object> user = userClient.getUserById(userId);

        if (user == null || !"HOST".equalsIgnoreCase((String) user.get("role"))) {
            throw new RuntimeException("Only verified hosts can create events");
        }

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

        int remainingTickets = event.getTotalTickets() - event.getTicketsSold();
        if (ticketsSold > remainingTickets) {
            throw new RuntimeException("Not enough tickets available for event ID: " + id);
        }

        event.setTicketsSold(event.getTicketsSold() + ticketsSold);
        return repo.save(event);
    }
}
