package com.bibliomanager.repository;

import com.bibliomanager.model.Book;
import com.bibliomanager.model.Category;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository {

    // FOR DASHBOARD VIEW
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

    // FOR BOOKS MANAGEMENT VIEW
    public List<Book> findAll() {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT b.*, c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc " +
                "FROM books b " +
                "LEFT JOIN categories c ON b.category_id = c.id " +
                "ORDER BY b.title";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                ) {
            while (resultSet.next())
                bookList.add(mapResultSetToBook(resultSet));
        } catch (SQLException e) {
            System.out.println("Error fetching books : " + e.getMessage());
            e.printStackTrace();
        }
        return bookList;
    }

    public List<Book> search(String query, Long categoryId, String status) {
        List<Book> bookList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT b.id, b.title, b.author, b.isbn,
                   b.cover_image, b.total_quantity, b.available_quantity,
                   c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc
            FROM books b
            JOIN categories c ON b.category_id = c.id
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" AND (LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ? OR b.isbn LIKE ?)");
            String q = "%" + query.toLowerCase() + "%";
            params.add(q); params.add(q); params.add(q);
        }
        if (categoryId != null) {
            sql.append(" AND b.category_id = ?");
            params.add(categoryId);
        }
        if (status != null) {
            switch (status) {
                case "Available"    -> sql.append(" AND b.available_quantity > 0 AND b.available_quantity >= b.total_quantity / 2");
                case "Low stock"    -> sql.append(" AND b.available_quantity > 0 AND b.available_quantity < b.total_quantity / 2");
                case "Out of stock" -> sql.append(" AND b.available_quantity = 0");
            }
        }
        sql.append(" ORDER BY b.title");
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
                ) {
            for (int i = 0; i < params.size(); i++)
                preparedStatement.setObject(i + 1, params.get(i));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) bookList.add(mapResultSetToBook(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Error searching books : " + e.getMessage());
            e.printStackTrace();
        }
        return bookList;
    }


    public void insert(Book book) throws SQLException {
        String sql = """
            INSERT INTO books (title, author, isbn, category_id,
                               cover_image, total_quantity, available_quantity)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try(
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setLong(4, book.getCategory().getId());
            ps.setString(5, book.getCoverImage());
            ps.setInt(6, book.getTotalQuantity());
            ps.setInt(7, book.getAvailableQuantity());
            ps.executeUpdate();
        }
    }

    public void update(Book book) {
        String sql = """
            UPDATE books SET title=?, author=?, isbn=?, category_id=?,
                             cover_image=?, total_quantity=?, available_quantity=?
            WHERE id=?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setLong(4, book.getCategory().getId());
            ps.setString(5, book.getCoverImage());
            ps.setInt(6, book.getTotalQuantity());
            ps.setInt(7, book.getAvailableQuantity());
            ps.setLong(8, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    //FOR BORROWS VIEW
    public void decrementAvailability(long bookId) throws SQLException {
        String sql = """
            UPDATE books SET available_quantity = available_quantity - 1
            WHERE id = ? AND available_quantity > 0
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setLong(1, bookId);
            ps.executeUpdate();
        }
    }

    public void incrementAvailability(long bookId) throws SQLException {
        String sql = """
            UPDATE books SET available_quantity = available_quantity + 1
            WHERE id = ? AND available_quantity < total_quantity
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setLong(1, bookId);
            ps.executeUpdate();
        }
    }

    // helper method to transform DB record into a Book Object
    public Book mapResultSetToBook (ResultSet resultSet) throws SQLException {
        Category cat = new Category(
                resultSet.getLong("cat_id"),
                resultSet.getString("cat_name"),
                resultSet.getString("cat_desc")
        );
        return new Book(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("isbn"),
                cat,
                resultSet.getString("cover_image"),
                resultSet.getInt("total_quantity"),
                resultSet.getInt("available_quantity")
        );
    }
}
