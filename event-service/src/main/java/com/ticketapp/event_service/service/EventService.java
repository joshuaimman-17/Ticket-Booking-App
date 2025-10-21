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

    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    public Event addEvent(Event e) {
        return repo.save(e);
    }

    public Event getEventById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public void deleteEvent(UUID id) {
        repo.deleteById(id);
    }
}
