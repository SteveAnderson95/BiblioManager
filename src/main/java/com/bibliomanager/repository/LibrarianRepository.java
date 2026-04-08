package com.bibliomanager.repository;

import com.bibliomanager.model.Librarian;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarianRepository {

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
}
