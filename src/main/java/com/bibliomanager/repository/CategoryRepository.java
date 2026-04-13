package com.bibliomanager.repository;

import com.bibliomanager.model.Category;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
