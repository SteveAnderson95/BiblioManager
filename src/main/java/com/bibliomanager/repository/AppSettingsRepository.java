package com.bibliomanager.repository;

import com.bibliomanager.model.AppSettings;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;

public class AppSettingsRepository {

    private Connection conn() throws SQLException {
        return DatabaseManager.getConnection();
    }

    public AppSettings load() throws SQLException {
        String sql = "SELECT key, value FROM app_settings";
        AppSettings settings = new AppSettings("BiblioManager", "", 14, 3);
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                switch (rs.getString("key")) {
                    case "library_name" -> settings.setLibraryName(rs.getString("value"));
                    case "library_address" -> settings.setLibraryAddress(rs.getString("value"));
                    case "default_loan_duration"-> settings.setDefaultLoanDuration(Integer.parseInt(rs.getString("value")));
                    case "max_loans_per_student"-> settings.setMaxLoansPerStudent(Integer.parseInt(rs.getString("value")));
                }
            }
        }
        return settings;
    }

    public void save(AppSettings settings) throws SQLException {
        String sql = "INSERT OR REPLACE INTO app_settings (key, value) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            upsert(ps, "library_name", settings.getLibraryName());
            upsert(ps, "library_address", settings.getLibraryAddress());
            upsert(ps, "default_loan_duration", String.valueOf(settings.getDefaultLoanDuration()));
            upsert(ps, "max_loans_per_student", String.valueOf(settings.getMaxLoansPerStudent()));
        }
    }

    private void upsert(PreparedStatement ps, String key, String value) throws SQLException {
        ps.setString(1, key);
        ps.setString(2, value);
        ps.executeUpdate();
    }

    public void resetLoans() throws SQLException {
        String sql = "DELETE FROM loans";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.executeUpdate();
        }
        // On remet les quantités disponibles à égal au total
        String resetBooks = " UPDATE books SET available_quantity = total_quantity ";
        try (PreparedStatement ps = conn().prepareStatement(resetBooks)) {
            ps.executeUpdate();
        }
    }

    public void resetAll() throws SQLException {
        try (Statement st = conn().createStatement()) {
            st.execute("DELETE FROM loans");
            st.execute("DELETE FROM books");
            st.execute("DELETE FROM students");
            st.execute("DELETE FROM categories");
            st.execute("DELETE FROM app_settings");
        }
    }
}