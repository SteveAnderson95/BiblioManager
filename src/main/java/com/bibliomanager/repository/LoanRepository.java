package com.bibliomanager.repository;

import com.bibliomanager.model.Book;
import com.bibliomanager.model.Loan;
import com.bibliomanager.model.Student;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LoanRepository {

    public int countOngoingLoans() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'ONGOING' OR status = 'OVERDUE'";
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

    public int countOverdueLoans() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'OVERDUE'";
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

    public Map<String, Integer> getLoansPerDayLastWeek() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        //I just get the 7 last days to be sure i gonna have 0 if no loans.
        String sql = "SELECT date(loan_date) as day, COUNT(id) as count " +
                "FROM loans WHERE loan_date >= date('now', '-7 days') " +
                "GROUP BY day ORDER BY day ASC";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            while (resultSet.next())
                stats.put(resultSet.getString("day"), resultSet.getInt("count"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Loan> findRecentLoans(int limit) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, s.first_name, s.last_name, b.title " +
                "FROM loans l " +
                "JOIN students s ON l.student_id = s.id " +
                "JOIN books b ON l.book_id = b.id " +
                "ORDER BY l.loan_date DESC LIMIT ?";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, s.first_name, s.last_name, b.title " +
                "FROM loans l " +
                "JOIN students s ON l.student_id = s.id " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.status = 'OVERDUE' ORDER BY l.due_date ASC";
        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
                ) {
            while (resultSet.next())
                loans.add(mapResultSetToLoan(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    //helper method to transform DB record into a Loan Object
    public Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Student student = new Student(rs.getLong("student_id"), null, rs.getString("first_name"), rs.getString("last_name"), "", "");
        Book book = new Book(rs.getLong("book_id"), rs.getString("title"), "", "", null, "", 0, 0);

        return new Loan(
                rs.getLong("id"),
                student,
                book,
                null,
                LocalDate.parse(rs.getString("loan_date")),
                LocalDate.parse(rs.getString("due_date")),
                rs.getString("return_date") != null ? LocalDate.parse(rs.getString("return_date")) : null
        );
    }
}
