package com.bibliomanager.repository;

import com.bibliomanager.model.Book;
import com.bibliomanager.model.Librarian;
import com.bibliomanager.model.Loan;
import com.bibliomanager.model.Student;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LoanRepository {

    public List<Loan> findAllLoans(String status, String period) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title,
                   lib.username AS librarian_username
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            JOIN librarians lib ON l.registered_by = lib.id
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (status != null && !status.equals("All status")) {
            sql.append(" AND l.status = ?");
            params.add(status.toUpperCase());
        }
        if (period != null) {
            switch (period) {
                case "Today" -> sql.append(" AND l.loan_date = date('now')");
                case "This week" -> sql.append(" AND l.loan_date >= date('now', '-7 days')");
                case "This month" -> sql.append(" AND strftime('%Y-%m', l.loan_date) = strftime('%Y-%m', 'now')");
            }
        }
        sql.append(" ORDER BY l.loan_date DESC");

        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString())
                ) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) loans.add(mapRowWithLibrarian(rs));
            }
        }
        return loans;
    }

    public List<Loan> searchAllLoans(String query, String status, String period)
            throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title,
                   lib.username AS librarian_username
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            JOIN librarians lib ON l.registered_by = lib.id
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append("""
                 AND (LOWER(s.first_name) LIKE ?
                 OR LOWER(s.last_name) LIKE ?
                 OR LOWER(b.title) LIKE ?)
            """);
            String q = "%" + query.toLowerCase() + "%";
            params.add(q); params.add(q); params.add(q);
        }
        if (status != null && !status.equals("All status")) {
            sql.append(" AND l.status = ?");
            params.add(status.toUpperCase());
        }
        if (period != null) {
            switch (period) {
                case "Today" -> sql.append(" AND l.loan_date = date('now')");
                case "This week" -> sql.append(" AND l.loan_date >= date('now', '-7 days')");
                case "This month" -> sql.append(" AND strftime('%Y-%m', l.loan_date) = strftime('%Y-%m', 'now')");
            }
        }
        sql.append(" ORDER BY l.loan_date DESC");

        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString())
                ) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) loans.add(mapRowWithLibrarian(rs));
            }
        }
        return loans;
    }

    // mapRow with librarian_username
    private Loan mapRowWithLibrarian(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getLong("student_id"), null,
                rs.getString("last_name"),
                rs.getString("first_name"),
                "", ""
        );
        Book book = new Book(
                rs.getLong("book_id"),
                rs.getString("title"),
                "", "", null, "", 0, 0
        );
        Librarian librarian = new Librarian(
                0, "", "",
                rs.getString("librarian_username"),
                "", ""
        );
        return new Loan(
                rs.getLong("id"),
                student, book, librarian,
                LocalDate.parse(rs.getString("loan_date")),
                LocalDate.parse(rs.getString("due_date")),
                rs.getString("return_date") != null ? LocalDate.parse(rs.getString("return_date")) : null
        );
    }

    public List<Loan> findReturnedLoans(String period, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'RETURNED'
        """);
        List<Object> params = new ArrayList<>();

        if (period != null) {
            switch (period) {
                case "Today" -> sql.append(" AND return_date = date('now')");
                case "This week" -> sql.append(" AND return_date >= date('now', 'weekday 0', '-7 days')");
                case "This month" -> sql.append(" AND strftime('%Y-%m', return_date) = strftime('%Y-%m', 'now')");
            }
        }
        if (type != null) {
            switch (type) {
                case "On time" -> sql.append(" AND return_date <= due_date");
                case "Late" -> sql.append(" AND return_date > due_date");
            }
        }
        sql.append(" ORDER BY l.return_date DESC");

        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ResultSet rs = ps.executeQuery()
                ) {
            while (rs.next()) loans.add(mapResultSetToLoan(rs));
        }
        return loans;
    }

    public List<Loan> searchReturnedLoans(String query, String period, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'RETURNED'
        """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append("""
                 AND (LOWER(s.first_name) LIKE ?
                   OR LOWER(s.last_name) LIKE ?
                   OR LOWER(b.title) LIKE ?)
            """);
            String q = "%" + query.toLowerCase() + "%";
            params.add(q); params.add(q); params.add(q);
        }
        if (period != null) {
            switch (period) {
                case "Today" -> sql.append(" AND return_date = date('now')");
                case "This week" -> sql.append(" AND return_date >= date('now', 'weekday 0', '-7 days')");
                case "This month" -> sql.append(" AND strftime('%Y-%m', return_date) = strftime('%Y-%m', 'now')");
            }
        }
        if (type != null) {
            switch (type) {
                case "On time" -> sql.append(" AND return_date <= due_date");
                case "Late" -> sql.append(" AND return_date > due_date");
            }
        }
        sql.append(" ORDER BY l.return_date DESC");

        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString())
                ) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public int countReturnedOnTime() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM loans
            WHERE status = 'RETURNED'
              AND return_date <= due_date
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countTotalReturned() throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'RETURNED'";
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Borrows due today (ONGOING with due_date = today)
    public int countExpectedReturnsToday() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM loans
            WHERE status IN ('ONGOING', 'OVERDUE')
              AND due_date <= date('now')
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Average number of days of delay on late returns
    public double averageOverdueDays() throws SQLException {
        String sql = """
            SELECT AVG(julianday(return_date) - julianday(due_date))
            FROM loans
            WHERE status = 'RETURNED'
              AND return_date > due_date
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    // Returns per day this week
    public Map<String, Integer> getReturnsPerDayThisWeek() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = """
            SELECT date(return_date) as day, COUNT(*) as count
            FROM loans
            WHERE status = 'RETURNED'
              AND return_date >= date('now', '-6 days')
            GROUP BY day
            ORDER BY day ASC
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            while (rs.next())
                stats.put(rs.getString("day"), rs.getInt("count"));
        }
        return stats;
    }

    public List<Loan> findActiveLoans() throws SQLException {
        String sql = """
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status IN ('ONGOING', 'OVERDUE')
            ORDER BY l.due_date ASC
        """;
        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            while (rs.next()) loans.add(mapResultSetToLoan(rs));
        }
        return loans;
    }

    public List<Loan> searchActiveLoans(String query, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status IN ('ONGOING', 'OVERDUE')
        """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append("""
                AND (LOWER(s.first_name) LIKE ?
                  OR LOWER(s.last_name)  LIKE ?
                  OR LOWER(b.title)      LIKE ?)
            """);
            String q = "%" + query.toLowerCase() + "%";
            params.add(q); params.add(q); params.add(q);
        }
        if (status != null && !status.equals("All status")) {
            sql.append(" AND l.status = ?");
            params.add(status.toUpperCase());
        }
        sql.append(" ORDER BY l.due_date ASC");

        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString())
                ) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public void insert(Loan loan) throws SQLException {
        String sql = """
            INSERT INTO loans
                (student_id, book_id, registered_by,
                 loan_date, due_date, return_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setLong(1, loan.getStudent().getId());
            ps.setLong(2, loan.getBook().getId());
            ps.setLong(3, loan.getRegisteredBy().getId());
            ps.setString(4, loan.getLoanDate().toString());
            ps.setString(5, loan.getDueDate().toString());
            ps.setString(6, loan.getReturnDate() != null ? loan.getReturnDate().toString() : null);
            ps.setString(7, loan.getStatus().toString());
            ps.executeUpdate();
        }
    }

    public void markAsReturned(long loanId, LocalDate returnDate) throws SQLException {
        String sql = """
            UPDATE loans
            SET return_date = ?, status = 'RETURNED'
            WHERE id = ?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, returnDate.toString());
            ps.setLong(2, loanId);
            ps.executeUpdate();
        }
    }

    public int countReturnedToday() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM loans
            WHERE status = 'RETURNED'
              AND return_date = date('now')
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Loan> findMostOverdue() throws SQLException {
        String sql = """
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'OVERDUE'
            ORDER BY l.due_date ASC
            LIMIT 5
        """;
        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) loans.add(mapResultSetToLoan(rs));
        }
        return loans;
    }

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

    public List<Loan> findActiveLoansByStudent(long studentId) throws SQLException {
        String sql = """
            SELECT l.id, l.loan_date, l.due_date, l.return_date, l.status,
                   s.id AS student_id, s.first_name, s.last_name,
                   b.id AS book_id, b.title
            FROM loans l
            JOIN students s ON l.student_id = s.id
            JOIN books b ON l.book_id = b.id
            WHERE l.student_id = ?
              AND l.status IN ('ONGOING', 'OVERDUE')
            ORDER BY l.due_date ASC
        """;
        List<Loan> loans = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) loans.add(mapResultSetToLoan(rs));
            }
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
