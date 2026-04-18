package com.bibliomanager.service;

import com.bibliomanager.model.Loan;
import com.bibliomanager.repository.BookRepository;
import com.bibliomanager.repository.LoanRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LoanService {

    private final LoanRepository repo = new LoanRepository();
    private final BookRepository bookRepo = new BookRepository();

    public List<Loan> getActiveLoans() {
        try {
            return repo.findActiveLoans();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active loans", e);
        }
    }

    public List<Loan> searchActiveLoans(String query, String status) {
        try {
            return repo.searchActiveLoans(query, status);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching loans", e);
        }
    }

    public void registerLoan(Loan loan) {
        validateLoan(loan);
        try {
            repo.insert(loan);
            // We decrement th book availability
            bookRepo.decrementAvailability(loan.getBook().getId());
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("Invalid student or book.");
            throw new RuntimeException("Error registering loan", e);
        }
    }

    public void markAsReturned(long loanId, long bookId) {
        try {
            repo.markAsReturned(loanId, LocalDate.now());
            // We increment again th book availability
            bookRepo.incrementAvailability(bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error marking loan as returned", e);
        }
    }

    public int getReturnedTodayCount() {
        try {
            return repo.countReturnedToday();
        } catch (SQLException e) {
            throw new RuntimeException("Error counting returns", e);
        }
    }

    public List<Loan> getMostOverdue() {
        try {
            return repo.findMostOverdue();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching most overdue", e);
        }
    }

    private void validateLoan(Loan loan) {
        if (loan.getStudent() == null)
            throw new IllegalArgumentException("Student is required.");
        if (loan.getBook() == null)
            throw new IllegalArgumentException("Book is required.");
        if (loan.getDueDate() == null)
            throw new IllegalArgumentException("Due date is required.");
        if (loan.getDueDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Due date must be in the future.");
        if (!loan.getBook().isAvailable())
            throw new IllegalArgumentException("This book is not available.");
    }

    public int getActiveLoansCount() {
        return repo.countOngoingLoans();
    }

    public int getOverdueLoansCount() {
        return repo.countOverdueLoans();
    }

    public Map<String, Integer> getWeeklyTrend() {
        return repo.getLoansPerDayLastWeek();
    }

    public List<Loan> getRecentBorrowings() {
        return repo.findRecentLoans(10);
    }

    public List<Loan> getOverdueAlerts() {
        return repo.findOverdueLoans();
    }

    public List<Loan> getActiveLoansByStudent(long studentId) {
        try {
            return repo.findActiveLoansByStudent(studentId);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching student loans", e);
        }
    }
}