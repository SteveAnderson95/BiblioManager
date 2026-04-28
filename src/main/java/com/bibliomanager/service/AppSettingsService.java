package com.bibliomanager.service;

import com.bibliomanager.model.AppSettings;
import com.bibliomanager.repository.AppSettingsRepository;

import java.sql.SQLException;

public class AppSettingsService {

    private final AppSettingsRepository repo = new AppSettingsRepository();

    public AppSettings getSettings() {
        try {
            return repo.load();
        } catch (SQLException e) {
            throw new RuntimeException("Error loading settings", e);
        }
    }

    public void saveSettings(AppSettings settings) {
        validate(settings);
        try {
            repo.save(settings);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving settings", e);
        }
    }

    public void resetLoans() {
        try {
            repo.resetLoans();
        } catch (SQLException e) {
            throw new RuntimeException("Error resetting loans", e);
        }
    }

    public void resetAll() {
        try {
            repo.resetAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error resetting database", e);
        }
    }

    private void validate(AppSettings s) {
        if (s.getLibraryName() == null || s.getLibraryName().isBlank())
            throw new IllegalArgumentException("Library name is required.");
        if (s.getDefaultLoanDuration() < 1)
            throw new IllegalArgumentException("Loan duration must be at least 1 day.");
        if (s.getMaxLoansPerStudent() < 1)
            throw new IllegalArgumentException("Max loans must be at least 1.");
    }
}