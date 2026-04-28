package com.bibliomanager.repository;

import com.bibliomanager.model.Librarian;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarianRepository {

    public Librarian findById(long id) throws SQLException {
        String sql = """
            SELECT id, first_name, last_name, email, username, password
            FROM librarians WHERE id = ?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void update(Librarian librarian) throws SQLException {
        String sql = """
            UPDATE librarians
            SET first_name=?, last_name=?, email=?, username=?
            WHERE id=?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, librarian.getFirstName());
            ps.setString(2, librarian.getLastName());
            ps.setString(3, librarian.getEmail());
            ps.setString(4, librarian.getUsername());
            ps.setLong(5, librarian.getId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(long id, String hashedPassword) throws SQLException {
        String sql = "UPDATE librarians SET password=? WHERE id=?";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, hashedPassword);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public Librarian findByUsername (String username) {
        String sql = "SELECT * FROM librarians WHERE username = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Librarian(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error searching for librarian : " + e.getMessage());
        }
        return null;
    }

    private Librarian mapRow(ResultSet rs) throws SQLException {
        return new Librarian(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
