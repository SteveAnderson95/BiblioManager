package com.bibliomanager.repository;

import com.bibliomanager.model.Student;
import com.bibliomanager.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public List<Student> findAll() throws SQLException {
        String sql = """
            SELECT id, student_number, first_name, last_name,
                   school_class, email
            FROM students
            ORDER BY last_name, first_name
        """;
        List<Student> list = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Student> search(String query, String schoolClass) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT id, student_number, first_name, last_name,
                   school_class, email
            FROM students
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append("""
                AND (LOWER(first_name) LIKE ?
                  OR LOWER(last_name)  LIKE ?
                  OR LOWER(student_number) LIKE ?
                  OR LOWER(email) LIKE ?)
            """);
            String q = "%" + query.toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }
        if (schoolClass != null && !schoolClass.isBlank()) {
            sql.append(" AND school_class = ?");
            params.add(schoolClass);
        }
        sql.append(" ORDER BY last_name, first_name");

        List<Student> list = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql.toString())
                ) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<String> findAllClasses() throws SQLException {
        String sql = "SELECT DISTINCT school_class FROM students ORDER BY school_class";
        List<String> classes = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ) {
            while (rs.next())
                classes.add(rs.getString("school_class"));
        }
        return classes;
    }

    public void insert(Student s) throws SQLException {
        String sql = """
            INSERT INTO students
                (student_number, first_name, last_name, school_class, email)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getSchoolClass());
            ps.setString(5, s.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = """
            UPDATE students
            SET student_number=?, first_name=?, last_name=?,
                school_class=?, email=?
            WHERE id=?
        """;
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getSchoolClass());
            ps.setString(5, s.getEmail());
            ps.setLong(6, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM students WHERE id = ?")
                ) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    //helper method to transform DB record into a Student Object
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getLong("id"),
                rs.getString("student_number"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("school_class"),
                rs.getString("email")
        );
    }
}