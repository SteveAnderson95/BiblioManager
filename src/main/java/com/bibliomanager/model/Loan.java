package com.bibliomanager.model;

import java.time.LocalDate;

public class Loan {

    private final long id;
    private Student student;
    private Book book;
    private Librarian registeredBy;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;

    public Loan(long id, Student student, Book book, Librarian registeredBy, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.student = student;
        this.book = book;
        this.registeredBy = registeredBy;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        if (returnDate != null)
            this.status = LoanStatus.RETURNED;
        else if(LocalDate.now().isAfter(dueDate))
            this.status = LoanStatus.OVERDUE;
        else {
            this.status = LoanStatus.ONGOING;
        }
    }

    public Loan(Student student, Book book, Librarian registeredBy, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = 0;
        this.student = student;
        this.book = book;
        this.registeredBy = registeredBy;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        if (returnDate != null)
            this.status = LoanStatus.RETURNED;
        else if(LocalDate.now().isAfter(dueDate))
            this.status = LoanStatus.OVERDUE;
        else {
            this.status = LoanStatus.ONGOING;
        }
    }

    // GETTERS


    public long getId() {
        return id;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    // SETTERS

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Librarian getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Librarian registeredBy) {
        this.registeredBy = registeredBy;
    }

    // METHODS

    public void calculateStatus (LocalDate currentDate) {

        if (returnDate != null)
            this.status = LoanStatus.RETURNED;
        else if (currentDate.isAfter(dueDate))
            this.status = LoanStatus.OVERDUE;
        else
            this.status = LoanStatus.ONGOING;
    }

    public void markAsReturned (LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = LoanStatus.RETURNED;
    }

    public boolean isOverdue () {
        return LocalDate.now().isAfter(dueDate) && status == LoanStatus.ONGOING;
    }
}
