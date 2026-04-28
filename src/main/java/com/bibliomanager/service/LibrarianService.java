package com.bibliomanager.service;

import com.bibliomanager.model.Librarian;
import com.bibliomanager.repository.LibrarianRepository;

import java.sql.SQLException;

public class LibrarianService {

    private final LibrarianRepository repo = new LibrarianRepository();

    public Librarian getById(long id) {
        try {
            return repo.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching librarian", e);
        }
    }

    public void updateProfile(Librarian librarian) {
        validate(librarian);
        try {
            repo.update(librarian);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new RuntimeException("Username or email already taken.");
            throw new RuntimeException("Error updating profile", e);
        }
    }

    public void updatePassword(long id, String currentPassword, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank())
            throw new IllegalArgumentException("New password is required.");
        if (!newPassword.equals(confirmPassword))
            throw new IllegalArgumentException("Passwords do not match.");
        if (newPassword.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        try {
            Librarian lib = repo.findById(id);
            if (lib == null)
                throw new RuntimeException("Librarian not found.");
            // check the current password
            if (!lib.getPassword().equals(currentPassword))
                throw new IllegalArgumentException("Current password is incorrect.");
            repo.updatePassword(id, newPassword);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password", e);
        }
    }

    private void validate(Librarian lib) {
        if (lib.getFirstName() == null || lib.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required.");
        if (lib.getLastName() == null || lib.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required.");
        if (lib.getUsername() == null || lib.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required.");
        if (lib.getEmail() == null || lib.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required.");
    }
}