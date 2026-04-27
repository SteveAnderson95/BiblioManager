package com.bibliomanager.repository;

import com.bibliomanager.model.Category;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public int getTotalCategoriesCount() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            if (resultSet.next())
                return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            while(resultSet.next()) {
                categories.add(new Category(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void insert(Category cat) throws SQLException {
        String sql = """
            INSERT INTO categories (name, description)
            VALUES (?, ?)
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Category cat) throws SQLException {
        String sql = """
            UPDATE categories
            SET name = ?, description = ?
            WHERE id = ?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            ps.setLong(3, cat.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}
