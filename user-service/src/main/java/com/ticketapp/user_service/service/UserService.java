package com.ticketapp.user_service.service;

import com.ticketapp.user_service.dto.HostApplicationDTO;
import com.ticketapp.user_service.entity.Role;
import com.ticketapp.user_service.entity.User;
import com.ticketapp.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // ---------- AUTH ----------

    public User register(User u) {
        if (repo.findByEmail(u.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        u.setRole(Role.USER);
        return repo.save(u);
    }

    public Optional<User> login(String email, String password) {
        return repo.findByEmail(email)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(password));
    }

    // ---------- HOST MANAGEMENT ----------

    @Transactional
    public User applyHost(UUID id, HostApplicationDTO dto) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setIdProofUrl(dto.getIdProofUrl());
        u.setPaypalEmail(dto.getPaypalEmail());
        u.setVerified(false);
        return repo.save(u);
    }

    @Transactional
    public User approveHost(UUID id) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setRole(Role.HOST);
        u.setVerified(true);
        return repo.save(u);
    }

    @Transactional
    public User rejectHost(UUID id) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setIdProofUrl(null);
        u.setPaypalEmail(null);
        u.setVerified(false);
        return repo.save(u);
    }

    // ---------- UTILITY ----------

    public List<User> getPendingHosts() {
        return repo.findAll().stream()
                .filter(u -> !u.isVerified() && u.getIdProofUrl() != null)
                .toList();
    }
    // Fetch all users with Role.HOST and verified
    public List<User> getApprovedHosts() {
        return repo.findAll().stream()
                .filter(u -> u.getRole() == Role.HOST && u.isVerified())
                .toList();
    }


    public Optional<User> findById(UUID id) {
        return repo.findById(id);
    }
}
