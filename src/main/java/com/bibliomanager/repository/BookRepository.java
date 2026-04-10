package com.bibliomanager.repository;

import com.bibliomanager.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class BookRepository {

    public int getTotalBooksCount() {
        String sql = "SELECT SUM(total_quantity) FROM books";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAvailableBooksCount() {
        String sql = "SELECT SUM(available_quantity) FROM books";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getBooksCountByCategory() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT c.name, COUNT(b.id) as total FROM books b " +
                "JOIN categories c ON b.category_id = c.id GROUP BY c.name";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            while (resultSet.next())
                stats.put(resultSet.getString("name"), resultSet.getInt("total"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
