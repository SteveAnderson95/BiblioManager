package com.bibliomanager.model;

public class Student extends Person{

    private String studentNumber;
    private String schoolClass;

    public Student (long id, String studentNumber, String firstName, String lastName, String schoolClass, String email) {
        super(id, firstName, lastName, email);
        this.studentNumber = studentNumber;
        this.schoolClass = schoolClass;
    }

    public Student (String studentNumber, String firstName, String lastName, String schoolClass, String email) {
        super(firstName, lastName, email);
        this.studentNumber = studentNumber;
        this.schoolClass = schoolClass;
    }

    // GETTERS

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    // SETTERS

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }
}
