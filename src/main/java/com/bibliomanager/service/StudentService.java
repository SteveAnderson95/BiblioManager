package com.bibliomanager.service;

import com.bibliomanager.model.Student;
import com.bibliomanager.repository.StudentRepository;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentRepository repo = new StudentRepository();

    public List<Student> getAllStudents() {
        try {
            return repo.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching students", e);
        }
    }

    public List<Student> searchStudents(String query, String schoolClass) {
        try {
            return repo.search(query, schoolClass);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching students", e);
        }
    }

    public List<String> getAllClasses() {
        try {
            return repo.findAllClasses();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching classes", e);
        }
    }

    public void addStudent(Student student) {
        validate(student);
        try {
            repo.insert(student);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new RuntimeException("Student number already exists.");
            throw new RuntimeException("Error adding student", e);
        }
    }

    public void updateStudent(Student student) {
        validate(student);
        try {
            repo.update(student);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new RuntimeException("Student number already exists.");
            throw new RuntimeException("Error updating student", e);
        }
    }

    public void deleteStudent(long id) {
        try {
            repo.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("Cannot delete: student has active loans.");
            throw new RuntimeException("Error deleting student", e);
        }
    }

    private void validate(Student s) {
        if (s.getFirstName() == null || s.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required.");
        if (s.getLastName() == null || s.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required.");
        if (s.getStudentNumber() == null || s.getStudentNumber().isBlank())
            throw new IllegalArgumentException("Student number is required.");
        if (s.getSchoolClass() == null || s.getSchoolClass().isBlank())
            throw new IllegalArgumentException("Class is required.");
    }
}