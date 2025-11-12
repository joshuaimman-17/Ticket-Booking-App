package com.ticketapp.admin_service.service;

import com.ticketapp.admin_service.entity.Admin;
import com.ticketapp.admin_service.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminService(AdminRepository repo) {
        this.repo = repo;
    }

    public Admin signup(Admin admin, String password) {
        if (admin.getEmail() == null || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        admin.setPasswordHash(encoder.encode(password));
        return repo.save(admin);
    }

    public Optional<Admin> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean validatePassword(Admin admin, String password) {
        return encoder.matches(password, admin.getPasswordHash());
    }
}
